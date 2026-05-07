package Auction.example.model.auction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import Auction.example.exception.AuctionClosedException;
import Auction.example.exception.InvalidBidException;
import Auction.example.observer.AuctionObserver;

class AuctionTest {

    private Auction auction;
    private TestObserver testObserver;

    // Lớp Mock Observer dùng để kiểm tra xem các sự kiện có được bắn ra đúng không
    class TestObserver implements AuctionObserver {
        boolean isStarted = false;
        boolean isCanceled = false;
        boolean isFinished = false;
        boolean isBidPlaced = false;
        double lastBidAmount = 0.0;
        String lastBidderId = "";

        @Override
        public void onAuctionStarted(String auctionId) {
            isStarted = true;
        }

        @Override
        public void onAuctionCanceled(String auctionId, String reason) {
            isCanceled = true;
        }

        @Override
        public void onAuctionFinished(String auctionId, String winnerId, double finalPrice) {
            isFinished = true;
        }

        @Override
        public void onBidPlaced(String auctionId, String bidderId, double amount) {
            isBidPlaced = true;
            this.lastBidAmount = amount;
            this.lastBidderId = bidderId;
        }
    }

    @BeforeEach
    void setUp() {
        // Khởi tạo phiên đấu giá: Giá khởi điểm 100, thời gian 60 phút, bước giá 10
        auction = new Auction("AUC-001", "SELLER_99", 100.0, 60, 10.0);
        testObserver = new TestObserver();
        auction.addObserver(testObserver); // Gắn observer vào để lắng nghe
    }

    // Test trạng thái

    @Test
    void testStartAuction() {
        assertEquals(Auction.State.OPEN, auction.getState(), "Trạng thái ban đầu phải là OPEN");

        auction.start();

        assertEquals(Auction.State.RUNNING, auction.getState(), "Trạng thái sau khi start phải là RUNNING");
        assertNotNull(auction.getStartTime());
        assertNotNull(auction.getEndTime());
        assertTrue(testObserver.isStarted, "Observer phải nhận được sự kiện onAuctionStarted");
    }

    @Test
    void testCancelAuction() {
        auction.start();
        auction.cancel("Người bán muốn hủy");

        assertEquals(Auction.State.CANCELED, auction.getState(), "Trạng thái phải là CANCELED");
        assertTrue(testObserver.isCanceled, "Observer phải nhận được sự kiện onAuctionCanceled");
        assertNull(auction.getWinnerId(), "Hủy phiên thì không có người thắng");
    }

    @Test
    void testFinishAuction() throws Exception {
        auction.start();
        auction.placeBid("USER_A", 120.0); // USER_A đang giữ giá cao nhất

        auction.finish();

        assertEquals(Auction.State.FINISHED, auction.getState());
        assertEquals("USER_A", auction.getWinnerId(), "USER_A phải là người chiến thắng");
        assertTrue(testObserver.isFinished, "Observer phải nhận được sự kiện onAuctionFinished");
    }

    // test đặt giá

    @Test
    void testPlaceValidBid() throws Exception {
        auction.start();

        // Đặt giá hợp lệ: 100 (hiện tại) + 10 (bước giá) = 110. Đặt 120 là hợp lệ.
        auction.placeBid("BIDDER_1", 120.0);

        assertEquals(120.0, auction.getCurrentPrice());
        assertEquals("BIDDER_1", auction.getHighestBidderId());
        assertEquals(1, auction.getBidHistory().size(), "Lịch sử đấu giá phải tăng lên 1");

        // Kiểm tra Observer
        assertTrue(testObserver.isBidPlaced);
        assertEquals(120.0, testObserver.lastBidAmount);
        assertEquals("BIDDER_1", testObserver.lastBidderId);
    }

    @Test
    void testPlaceInvalidBid_TooLow() {
        auction.start();

        // Giá hiện tại 100, bước giá 10 -> tối thiểu phải là 110. Cố tình đặt 105.
        InvalidBidException exception = assertThrows(InvalidBidException.class, () -> {
            auction.placeBid("BIDDER_2", 105.0);
        });

        assertEquals(105.0, exception.getAttemptedAmount());
        assertEquals(110.0, exception.getCurrentPrice(), "Cần báo lỗi với mức giá tối thiểu được phép (100 + 10)");
    }

    @Test
    void testPlaceBid_AuctionNotRunning() {
        // Phiên đấu giá đang OPEN (chưa start)
        assertThrows(AuctionClosedException.class, () -> {
            auction.placeBid("BIDDER_1", 150.0);
        });

        auction.cancel("Hủy");
        // Phiên đấu giá đã bị CANCELED
        assertThrows(AuctionClosedException.class, () -> {
            auction.placeBid("BIDDER_1", 150.0);
        });
    }

    // test thanh toán

    @Test
    void testProcessPayment_Success() throws Exception {
        auction.start();
        auction.placeBid("WINNER_01", 500.0);
        auction.finish(); // Phải FINISHED mới được thanh toán

        boolean isSuccess = auction.processPayment("WINNER_01", 500.0);

        assertTrue(isSuccess, "Thanh toán phải thành công");
        assertEquals(Auction.State.PAID, auction.getState(), "Trạng thái chuyển thành PAID");
    }

    @Test
    void testProcessPayment_FailCases() throws Exception {
        auction.start();
        auction.placeBid("WINNER_01", 500.0);

        // Case 1: Thanh toán khi chưa kết thúc (đang RUNNING)
        assertFalse(auction.processPayment("WINNER_01", 500.0), "Không thể thanh toán khi đang RUNNING");

        auction.finish();

        // Case 2: Sai người thắng (Hacker cố tình thanh toán)
        assertFalse(auction.processPayment("HACKER", 500.0), "Sai winnerId phải trả về false");

        // Case 3: Chuyển thiếu tiền
        assertFalse(auction.processPayment("WINNER_01", 499.0), "Thiếu tiền phải trả về false");
    }

    //test utility và getters
    @Test
    void testRemainingTime() {
        // Khi chưa start, trả về toàn bộ duration
        assertEquals(60 * 60 * 1000, auction.getRemainingTimeMillis());

        auction.start();
        long remaining = auction.getRemainingTimeMillis();

        assertTrue(remaining <= 60 * 60 * 1000 && remaining > 59 * 60 * 1000,
                "Thời gian còn lại phải xấp xỉ 60 phút sau khi vừa start");
    }
}
