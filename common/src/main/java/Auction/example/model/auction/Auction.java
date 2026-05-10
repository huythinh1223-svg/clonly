package Auction.example.model.auction;

import Auction.example.exception.AuctionClosedException;
import Auction.example.exception.InvalidBidException;
import Auction.example.model.item.items.Item;
import Auction.example.observer.AuctionObserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Auction implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final ScheduledExecutorService SHARED_EXECUTOR =
            Executors.newScheduledThreadPool(4);

    public enum State {
        OPEN,
        RUNNING,
        FINISHED,
        PAID,
        CANCELED
    }

    private String currentAuctionId;
    private State state;

    private String sellerId;
    private String highestBidderId;
    private String winnerId;

    private Item auctionItem;
    private double startPrice;
    private double currentPrice;

    private List<Bid> bidHistory;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long duration;

    private double minIncrementalPrice;

    private transient List<AuctionObserver> observers;
    private transient ScheduledExecutorService executor;
    private transient ScheduledFuture<?> future;

    public Auction(String currentAuctionId, String sellerId, double startPrice, long duration,
                   double minIncrementalPrice) {
        this(currentAuctionId, sellerId, null, startPrice, duration, minIncrementalPrice);
    }

    public Auction(String currentAuctionId, String sellerId, Item auctionItem, double startPrice,
                   long duration, double minIncrementalPrice) {
        this.currentAuctionId = currentAuctionId;
        this.state = State.OPEN;
        this.sellerId = sellerId;
        this.auctionItem = auctionItem;
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.duration = duration;
        this.minIncrementalPrice = minIncrementalPrice;
        this.bidHistory = new CopyOnWriteArrayList<>();
        restoreRuntimeState(false);
    }

    public synchronized void start() {
        if (this.state != State.OPEN) {
            return;
        }

        this.startTime = LocalDateTime.now();
        this.endTime = startTime.plusMinutes(duration);
        this.state = State.RUNNING;
        StartAuctionNotifier();
        autoCloseAuction();
    }

    public synchronized void cancel(String reason) {
        if (this.state == State.CANCELED || this.state == State.FINISHED || this.state == State.PAID) {
            return;
        }

        state = State.CANCELED;
        if (future != null && !future.isDone()) {
            future.cancel(false);
        }
        CancelAuctionNotifier(reason);
        cleanup();
    }

    private void autoCloseAuction() {
        if (endTime == null) {
            return;
        }

        long remainingMillis = Duration.between(LocalDateTime.now(), endTime).toMillis();
        if (remainingMillis > 0) {
            future = executor.schedule(this::finish, remainingMillis, TimeUnit.MILLISECONDS);
        } else {
            finish();
        }
    }

    public synchronized void finish() {
        if (this.state == State.FINISHED || this.state == State.PAID || this.state == State.CANCELED) {
            return;
        }

        state = State.FINISHED;
        if (highestBidderId != null) {
            winnerId = highestBidderId;
        }
        FinishAuctionNotifier(winnerId, currentPrice);
        cleanup();
    }

    public synchronized void placeBid(String bidderId, double amount)
            throws InvalidBidException, AuctionClosedException {
        if (state != State.RUNNING) {
            throw new AuctionClosedException("Auction is not running", currentAuctionId);
        }

        if (endTime != null && LocalDateTime.now().isAfter(endTime)) {
            finish();
            throw new AuctionClosedException("Auction is already ended", currentAuctionId);
        }

        if (amount < currentPrice + minIncrementalPrice) {
            throw new InvalidBidException("Your bid is too low", amount, currentPrice + minIncrementalPrice);
        }

        currentPrice = amount;
        highestBidderId = bidderId;

        Bid bid = new Bid(currentAuctionId, bidderId, amount);
        bidHistory.add(bid);
        BidPlacedNotifier(bidderId, amount);
    }

    public synchronized boolean processPayment(String winnerId, double amount) {
        if (state != State.FINISHED) {
            return false;
        }
        if (!winnerId.equals(this.winnerId)) {
            return false;
        }
        if (amount >= currentPrice) {
            state = State.PAID;
            return true;
        }
        return false;
    }

    public void addObserver(AuctionObserver observer) {
        if (observer == null) {
            return;
        }
        if (observers == null) {
            observers = new CopyOnWriteArrayList<>();
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(AuctionObserver observer) {
        if (observers != null) {
            observers.remove(observer);
        }
    }

    private void cleanup() {
        if (future != null && !future.isDone()) {
            future.cancel(false);
        }
        if (observers != null) {
            observers.clear();
        }
        future = null;
    }

    private void notifyObservers(java.util.function.Consumer<AuctionObserver> action) {
        if (observers == null) {
            return;
        }
        for (AuctionObserver observer : observers) {
            try {
                action.accept(observer);
            } catch (Exception e) {
                System.err.println("Observer error: " + e.getMessage());
            }
        }
    }

    public void BidPlacedNotifier(String bidderId, double amount) {
        notifyObservers(observer -> observer.onBidPlaced(currentAuctionId, bidderId, amount));
    }

    public void StartAuctionNotifier() {
        notifyObservers(observer -> observer.onAuctionStarted(currentAuctionId));
    }

    public void CancelAuctionNotifier(String reason) {
        notifyObservers(observer -> observer.onAuctionCanceled(currentAuctionId, reason));
    }

    public void FinishAuctionNotifier(String winnerId, double finalPrice) {
        notifyObservers(observer -> observer.onAuctionFinished(currentAuctionId, winnerId, finalPrice));
    }

    public void restoreAfterLoad() {
        restoreRuntimeState(true);
    }

    private void restoreRuntimeState(boolean resumeTimers) {
        // Các field transient không được ghi vào file .dat, nên phải tạo lại sau khi deserialize.
        this.executor = SHARED_EXECUTOR;
        this.observers = new CopyOnWriteArrayList<>();
        if (this.bidHistory == null) {
            this.bidHistory = new CopyOnWriteArrayList<>();
        } else if (!(this.bidHistory instanceof CopyOnWriteArrayList)) {
            this.bidHistory = new CopyOnWriteArrayList<>(this.bidHistory);
        }
        this.future = null;

        // Nếu server tắt khi phiên đang chạy, khi mở lại sẽ tiếp tục hẹn giờ đóng phiên.
        if (resumeTimers && state == State.RUNNING) {
            autoCloseAuction();
        }
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        // Chỉ khôi phục object runtime ở đây; DatabaseManager sẽ quyết định có resume timer hay không.
        restoreRuntimeState(false);
    }

    public State getState() {
        return state;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public List<Bid> getBidHistory() {
        return new ArrayList<>(bidHistory);
    }

    public String getHighestBidderId() {
        return highestBidderId;
    }

    public String getAuctionId() {
        return currentAuctionId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public Item getAuctionItem() {
        return auctionItem;
    }

    public void setAuctionItem(Item auctionItem) {
        this.auctionItem = auctionItem;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public double getMinIncrementalPrice() {
        return minIncrementalPrice;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public long getRemainingTimeMillis() {
        if (endTime == null) {
            return duration * 60 * 1000;
        }
        long remaining = Duration.between(LocalDateTime.now(), endTime).toMillis();
        return Math.max(0, remaining);
    }

    public long getDuration() {
        return duration;
    }
}
