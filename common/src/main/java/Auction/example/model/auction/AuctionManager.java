package Auction.example.model.auction;

import Auction.example.model.user.Bidder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionManager {
    private static volatile AuctionManager instance;

    private final Map<String, Auction> auctions = new ConcurrentHashMap<>();

    private AuctionManager() {}

    public static AuctionManager getInstance() {
        if (instance == null) {
            synchronized (AuctionManager.class) {
                if (instance == null) {
                    instance = new AuctionManager();
                }
            }
        }
        return instance;
    }

    public void addAuction(Auction auction) {
        if (auction == null) {
            throw new IllegalArgumentException("Auction must not be null");
        }
        // putIfAbsent giúp tránh ghi đè phiên đấu giá đã tồn tại trong RAM.
        Auction previous = auctions.putIfAbsent(auction.getAuctionId(), auction);
        if (previous != null) {
            throw new IllegalArgumentException("Auction ID already exists: " + auction.getAuctionId());
        }
    }

    public Auction getAuction(String auctionId) throws Exception {
        Auction auction = auctions.get(auctionId);
        if (auction == null) {
            throw new Exception("Auction not found: " + auctionId);
        }
        return auction;
    }

    public Optional<Auction> findById(String auctionId) {
        if (auctionId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(auctions.get(auctionId));
    }

    public void removeAuction(String auctionId) throws Exception {
        if (auctions.remove(auctionId) == null) {
            throw new Exception("Auction not found: " + auctionId);
        }
    }

    public Collection<Auction> getAllAuctions() {
        return Collections.unmodifiableCollection(auctions.values());
    }

    public void clear() {
        auctions.clear();
    }

    public void placeBid(String auctionId, Bidder bidder, double amount) throws Exception {
        if (bidder == null) {
            throw new IllegalArgumentException("Bidder must not be null");
        }
        getAuction(auctionId).placeBid(bidder.getId(), amount);
    }

    public void cancelAuction(String auctionId, String reason) throws Exception {
        getAuction(auctionId).cancel(reason);
    }

    public boolean processPayment(String auctionId, String winnerId, double amount) throws Exception {
        return getAuction(auctionId).processPayment(winnerId, amount);
    }

    public Collection<Auction> getAuctionsByState(Auction.State targetState) {
        return auctions.values().stream()
                .filter(auction -> auction.getState() == targetState)
                .toList();
    }
}
