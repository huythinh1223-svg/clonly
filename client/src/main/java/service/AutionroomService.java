package service;

import network.AuctionroomClient;
// import model.BidRequest;

public class AutionroomService {

    // Thêm tham số controller để Service có thể truyền tiếp cho Network
    public void processBid(Object controller, String auctionId, String bidderId, String inputAmount, double currentPrice) {
        try {
            double bidAmount = Double.parseDouble(inputAmount);

            // Kiểm tra logic cơ bản
            if (bidAmount <= currentPrice) {
                System.out.println("Lỗi: Số tiền đặt phải cao hơn giá hiện tại!");
                return;
            }

            // Mọi thứ hợp lệ -> Đóng gói thành Object (Sử dụng class Serializable của bạn)
            // Server yêu cầu format: PLACE_BID|auctionId|bidderId|amount
            String request = "PLACE_BID|" + auctionId + "|" + bidderId + "|" + bidAmount;
            // Gọi tầng Network để gửi đi
            AuctionroomClient networkClient = new AuctionroomClient(controller);
            networkClient.sendBidRequest(request);

        } catch (NumberFormatException e) {
            System.out.println("Lỗi: Vui lòng nhập số!");
        }
    }
}