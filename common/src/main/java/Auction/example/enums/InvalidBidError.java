package Auction.example.enums;
public enum InvalidBidError {
    PRICE_TOO_LOW("Giá đặt phải cao hơn mức giá hiện tại"),
    PRICE_BELOW_STARTING("Giá đặt không được thấp hơn giá khởi điểm"),
    BIDDER_IS_SELLER("Người bán không thể tự đặt giá cho sản phẩm của mình"),
    INVALID_INCREMENT("Bước giá không hợp lệ"); 

    private final String description;

    InvalidBidError(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
