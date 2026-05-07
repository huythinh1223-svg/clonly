package Auction.example.exception;

public class InvalidBidException extends AuctionException {

    private double attemptedAmount;
    private double currentPrice;

    public InvalidBidException(String message, double attemptedAmount, double currentPrice) {
        super(message, "INVALID_BID");
        this.attemptedAmount = attemptedAmount;
        this.currentPrice = currentPrice;
    }

    public double getAttemptedAmount() {
        return attemptedAmount;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }
}