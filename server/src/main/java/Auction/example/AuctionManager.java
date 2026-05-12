package Auction.example;

import Auction.example.exceptions.AuctionClosedException;
import Auction.example.exceptions.InvalidBidException;
import Auction.example.exceptions.UnauthorizedBidException;
import Auction.example.interfaces.WalletService;
import Auction.example.model.item.items.Item;
import Auction.example.observer.AuctionObserver;
import Auction.example.enums.State;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AuctionManager {

    private final WalletService walletService;

    public interface AuctionManagerObserver {
        default void onAuctionCreated(String auctionId, String sellerId) {}
        default void onAuctionRemoved(String auctionId, State finalState) {}
    }

    private final ConcurrentHashMap<String, Auction> auctions = new ConcurrentHashMap<>();
    private final List<AuctionManagerObserver> globalObservers = new CopyOnWriteArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    private final String idPrefix;

    public AuctionManager(WalletService walletService) {
        this("AUCTION-", walletService);
    }

    public AuctionManager(String idPrefix, WalletService walletService) {
        this.walletService = walletService;
        this.idPrefix = idPrefix;
    }

    public Auction createAuction(String sellerId,
                                 double startPrice,
                                 long durationMinutes,
                                 double minIncrementalPrice, Item auctionItem) {
        String auctionId = idPrefix + idCounter.getAndIncrement();

        Auction auction = new Auction(
                auctionId,
                sellerId,
                startPrice,
                durationMinutes,
                minIncrementalPrice,
                auctionItem
        );

        Auction existing = auctions.putIfAbsent(auctionId, auction);
        if (existing != null) {
            throw new IllegalStateException("Duplicate auction ID generated: " + auctionId);
        }

        notifyGlobalObservers(o -> o.onAuctionCreated(auctionId, sellerId));
        return auction;
    }

    public void startAuction(String auctionId) {
        Auction auction = getAuctionOrThrow(auctionId);

        if (auction.getState() != State.OPEN) {
            throw new IllegalStateException(
                    "Auction " + auctionId + " cannot be started from state: " + auction.getState());
        }

        auction.start();
    }

    public void cancelAuction(String auctionId, String reason) {
        Auction auction = getAuctionOrThrow(auctionId);

        auction.cancel(reason);
        releaseHighestBidReserve(auction);

        notifyGlobalObservers(o -> o.onAuctionRemoved(auctionId, State.CANCELED));
    }

    public void finishAuction(String auctionId) {
        Auction auction = getAuctionOrThrow(auctionId);

        auction.finish();

        notifyGlobalObservers(o -> o.onAuctionRemoved(auctionId, State.FINISHED));
    }

    public boolean removeAuction(String auctionId) {
        Auction auction = auctions.get(auctionId);
        if (auction == null) return false;

        State state = auction.getState();

        if (!canRemoveAuction(auction)) {
            throw new IllegalStateException(
                    "Cannot remove auction " + auctionId + " (state=" + state + "). " +
                            "Finish payment or cancel it first.");
        }

        boolean removed = auctions.remove(auctionId, auction);
        if (removed) {
            notifyGlobalObservers(o -> o.onAuctionRemoved(auctionId, state));
        }
        return removed;
    }

    public void placeBid(String auctionId, String bidderId, double amount)
            throws AuctionClosedException, InvalidBidException, UnauthorizedBidException {

        Auction auction = getAuctionOrThrow(auctionId);

        String previousBidder = auction.getHighestBidderId();
        double previousPrice = auction.getCurrentPrice();

        boolean sameBidder = previousBidder != null && previousBidder.equals(bidderId);

        if (sameBidder) {
            walletService.releaseFunds(previousBidder, previousPrice);

            if (!walletService.reserveFunds(bidderId, amount)) {
                walletService.reserveFunds(previousBidder, previousPrice);
                throw new IllegalStateException("Insufficient balance");
            }

            try {
                auction.placeBid(bidderId, amount);
            } catch (Exception e) {
                walletService.releaseFunds(bidderId, amount);
                walletService.reserveFunds(previousBidder, previousPrice);
                throw e;
            }

            return;
        }

        boolean reserved = walletService.reserveFunds(bidderId, amount);
        if (!reserved) {
            throw new IllegalStateException("Insufficient balance");
        }

        try {
            auction.placeBid(bidderId, amount);

            if (previousBidder != null) {
                walletService.releaseFunds(previousBidder, previousPrice);
            }

        } catch (Exception e) {
            walletService.releaseFunds(bidderId, amount);
            throw e;
        }
    }

    public boolean processPayment(String auctionId, String winnerId, double amount) {
        Auction auction = getAuctionOrThrow(auctionId);

        boolean accepted = auction.processPayment(winnerId, amount);
        if (accepted) {
            walletService.captureFunds(winnerId, amount);
        }

        return accepted;
    }

    public void addObserverToAuction(String auctionId, AuctionObserver observer) {
        Auction auction = auctions.get(auctionId);
        if (auction != null) auction.addObserver(observer);
    }

    public void removeObserverFromAuction(String auctionId, AuctionObserver observer) {
        Auction auction = auctions.get(auctionId);
        if (auction != null) auction.removeObserver(observer);
    }

    public void addGlobalObserver(AuctionManagerObserver observer) {
        globalObservers.add(observer);
    }

    public void removeGlobalObserver(AuctionManagerObserver observer) {
        globalObservers.remove(observer);
    }

    public Optional<Auction> findAuction(String auctionId) {
        return Optional.ofNullable(auctions.get(auctionId));
    }

    public List<Auction> getAuctionsByState(State state) {
        return auctions.values().stream()
                .filter(a -> a.getState() == state)
                .collect(Collectors.toList());
    }

    public List<Auction> getAuctionsBySeller(String sellerId) {
        return auctions.values().stream()
                .filter(a -> sellerId.equals(a.getSellerId()))
                .collect(Collectors.toList());
    }

    public int getAuctionCount() {
        return auctions.size();
    }

    public long getRunningCount() {
        return auctions.values().stream()
                .filter(a -> a.getState() == State.RUNNING)
                .count();
    }

    public void cancelAll(String reason) {
        auctions.values().forEach(a -> {
            State s = a.getState();
            if (s == State.OPEN || s == State.RUNNING) {
                a.cancel(reason);
                releaseHighestBidReserve(a);
                notifyGlobalObservers(o -> o.onAuctionRemoved(a.getAuctionId(), State.CANCELED));
            }
        });
    }

    public int purgeTerminated() {
        List<String> toRemove = auctions.entrySet().stream()
                .filter(e -> canRemoveAuction(e.getValue()))
                .map(java.util.Map.Entry::getKey)
                .collect(Collectors.toList());

        int count = 0;
        for (String id : toRemove) {
            if (auctions.remove(id) != null) count++;
        }
        return count;
    }

    private Auction getAuctionOrThrow(String auctionId) {
        Auction auction = auctions.get(auctionId);
        if (auction == null) {
            throw new IllegalArgumentException("Auction not found: " + auctionId);
        }
        return auction;
    }

    private boolean canRemoveAuction(Auction auction) {
        State state = auction.getState();

        if (state == State.PAID || state == State.CANCELED) {
            return true;
        }

        return state == State.FINISHED && auction.getHighestBidderId() == null;
    }

    private void releaseHighestBidReserve(Auction auction) {
        String highestBidderId = auction.getHighestBidderId();
        if (highestBidderId == null) {
            return;
        }

        walletService.releaseFunds(highestBidderId, auction.getCurrentPrice());
    }

    private void notifyGlobalObservers(Consumer<AuctionManagerObserver> action) {
        for (AuctionManagerObserver observer : globalObservers) {
            try {
                action.accept(observer);
            } catch (Exception e) {
                System.err.println("[AuctionManager] Global observer error: " + e.getMessage());
            }
        }
    }
}