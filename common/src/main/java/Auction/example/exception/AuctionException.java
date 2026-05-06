package Auction.example.exception;

public class AuctionException extends Exception {
    private String errorCode;

    public AuctionException(String message) {
        super(message);
    }

    public AuctionException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}