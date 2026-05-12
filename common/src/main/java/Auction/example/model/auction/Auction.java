package Auction.example.model.auction;

import Auction.example.enums.InvalidBidError;
import Auction.example.model.item.items.Item;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import Auction.example.exceptions.InvalidBidException;
import Auction.example.exceptions.AuctionClosedException;
import Auction.example.exceptions.AuctionException;
import Auction.example.observer.AuctionObserver;

public class Auction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum State {
        OPEN, // moi tao phien dau gia
        RUNNING, // phien dau gia dang dien ra
        FINISHED, // phien dau gia ket thuc
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
    private long duration; // tinh theo minutes

    private double minIncrementalPrice;

    private static final ScheduledExecutorService SHARED_EXECUTOR =
            Executors.newScheduledThreadPool(4);

    private transient List<AuctionObserver> observers;
    private transient ScheduledExecutorService executor;
    private transient ScheduledFuture<?> future; // nhiem vu tu dong close auction trong tuong lai

    public Auction(String currentAuctionId, String sellerId, double startPrice, long duration,
                   double minIncrementalPrice) {
        this.currentAuctionId = currentAuctionId;
        this.state = State.OPEN;
        this.sellerId = sellerId;
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.minIncrementalPrice = minIncrementalPrice;
        this.executor = SHARED_EXECUTOR;
        this.duration = duration;

        this.bidHistory = new CopyOnWriteArrayList<>();
    }

    // Start the auction
    public synchronized void start(){
        if (this.state != State.OPEN) {
            return;
        }

        this.startTime = LocalDateTime.now();
        this.endTime = startTime.plusMinutes(duration);

         this.state = State.RUNNING;
         StartAuctionNotifier();
         autoCloseAuction();
    }

    public synchronized void cancel(String reason){
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

    // Auto close the auction if khong ai bid them hoac het thoi gian
    private void autoCloseAuction() {
        long remainingMillis = Duration.between(LocalDateTime.now(), endTime).toMillis();

        if (remainingMillis > 0) {
            future = executor.schedule(this::finish, remainingMillis, TimeUnit.MILLISECONDS);
        } else {
            finish();
        }
    }

    //Ket thuc phien dau gia
    public synchronized void finish(){
        if (this.state == State.FINISHED || this.state == State.PAID ||  this.state == State.CANCELED) {
            return;
        }

        state = State.FINISHED;

        if (highestBidderId != null){
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

        if (LocalDateTime.now().isAfter(endTime)) {
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
        if (state != State.FINISHED) return false;

        if (!winnerId.equals(this.winnerId)) {
            return false;
        }

        if (Math.abs(amount - currentPrice) < 0.01) {
            state = State.PAID;
            return true;
        }
        return false;
    }

    // Tao observer
    public void addObserver(AuctionObserver observer) {
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
            observers = null;
        }

        future = null;
    }

    //wrap observer vao try catch de ko bi crash khi 1 cai bi loi
    private void notifyObservers(java.util.function.Consumer<AuctionObserver> action) {
        if (observers == null) return;

        for (AuctionObserver observer : observers) {
            try {
                action.accept(observer);
            } catch (Exception e) {
                System.err.println("Observer error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void BidPlacedNotifier(String bidderId, double amount) {
        notifyObservers(o -> o.onBidPlaced(currentAuctionId, bidderId, amount));
    }

    public void StartAuctionNotifier() {
        notifyObservers(o -> o.onAuctionStarted(currentAuctionId));
    }

    public void CancelAuctionNotifier(String reason) {
        notifyObservers(o -> o.onAuctionCanceled(currentAuctionId, reason));
    }

    public void FinishAuctionNotifier(String winnerId, double finalPrice) {
        notifyObservers(o -> o.onAuctionFinished(currentAuctionId, winnerId, finalPrice));
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