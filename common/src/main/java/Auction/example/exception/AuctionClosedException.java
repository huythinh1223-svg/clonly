package Auction.example.exception;

public class AuctionClosedException extends AuctionException {

    private String auctionId;

    public AuctionClosedException(String message, String auctionId) {
        super(message, "AUCTION_CLOSED");
        this.auctionId = auctionId;
    }

    public String getAuctionId() {
        return auctionId;
    }
}