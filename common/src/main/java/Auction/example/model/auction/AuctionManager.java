package Auction.example.model.auction;

import Auction.example.model.user.Bidder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionManager {

    // --- Singleton ---
    private static volatile AuctionManager instance;

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

    // --- Dữ liệu ---
    private final Map<String, Auction> auctions = new ConcurrentHashMap<>();

    // --- CRUD ---
    public void addAuction(Auction auction) {
        if (auctions.containsKey(auction.getAuctionId())) {
            throw new IllegalArgumentException("Auction ID already exists: " + auction.getAuctionId());
        }
        auctions.put(auction.getAuctionId(), auction);
    }

    public Auction getAuction(String auctionId) throws Exception {
        Auction auction = auctions.get(auctionId);
        if (auction == null) {
            throw new Exception("Auction not found: " + auctionId);
        }
        return auction;
    }

    public void removeAuction(String auctionId) throws Exception {
        if (auctions.remove(auctionId) == null) {
            throw new Exception("Auction not found: " + auctionId);
        }
    }

    public Collection<Auction> getAllAuctions() {
        return Collections.unmodifiableCollection(auctions.values());
    }

    // --- Business logic ---
    public void placeBid(String auctionId, Bidder bidder, double amount) throws Exception {
        getAuction(auctionId).placeBid(bidder.getId(), amount);
    }

    public void cancelAuction(String auctionId,String reason) throws Exception {
        getAuction(auctionId).cancel(reason);
    }

    public boolean processPayment(String auctionId, String winnerId, double amount) throws Exception {
        return getAuction(auctionId).processPayment(winnerId, amount);
    }

    // Lấy tất cả auction theo trạng thái
    public Collection<Auction> getAuctionsByState(Auction.State targetState) {
        return auctions.values().stream()
                .filter(a -> a.getState() == targetState)
                .toList();
    }
}