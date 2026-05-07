package Auction.example.observer;

import Auction.example.model.auction.Bid;

public interface AuctionObserver {

    void onBidPlaced(String auctionId, String bidderId, double amount);

    void onAuctionStarted(String auctionId);

    void onAuctionCanceled(String auctionId, String reason);

    void onAuctionFinished(String auctionId, String winnerId, double finalPrice);


}