package user.code.src;
package java;
import java.AuctionClosedException;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionService {
    private double currentPrice;
    private String highestBidder;
    private boolean isClosed; // Trạng thái phiên
    private final ReentrantLock lock = new ReentrantLock();

    public AuctionService(double startPrice) {
        this.currentPrice = startPrice;
        this.isClosed = false;
    }

     // Hàm đặt giá

    public void placeBid(String bidderName, double bidAmount) throws AuctionClosedException {
        lock.lock();
        try {
            // Kiểm tra ngoại lệ AuctionClosedException
            if (isClosed) {
                throw new AuctionClosedException("Không thể đặt giá: Phiên đấu giá đã kết thúc!");
            }

           //Kiểm tra giá hợp lệ
            if (bidAmount > currentPrice) {
                this.currentPrice = bidAmount;
                this.highestBidder = bidderName;
                System.out.println("Đặt giá thành công: " + bidderName + " - " + bidAmount);
            } else {
                System.out.println("Giá đặt không hợp lệ.");
            }
        } finally {
            lock.unlock(); // Luôn giải phóng khóa để tránh treo ứng dụng
        }
    }

    // Hàm đóng phiên
    public void setClosed(boolean closed) {
        this.isClosed = closed;
    }
}
