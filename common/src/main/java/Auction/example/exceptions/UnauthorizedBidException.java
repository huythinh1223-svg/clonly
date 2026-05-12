package Auction.example.exceptions;

import Auction.example.exceptions.AuctionException;

public class UnauthorizedBidException extends AuctionException {

    private final String userId;
    private final String auctionId;

    public UnauthorizedBidException(String message,
                                    String userId,
                                    String auctionId) {
        super(message);
        this.userId = userId;
        this.auctionId = auctionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getAuctionId() {
        return auctionId;
    }

    @Override
    public String toString() {
        return "UnauthorizedBidException{" +
                "message='" + getMessage() + '\'' +
                ", userId='" + userId + '\'' +
                ", auctionId='" + auctionId + '\'' +
                '}';
    }
}