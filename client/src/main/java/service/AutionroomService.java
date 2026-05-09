package service;

import network.AuctionroomClient;
// import model.BidRequest;

public class AutionroomService {

    // Thêm tham số controller để Service có thể truyền tiếp cho Network
    public void processBid(Object controller, String username, String productName, String inputAmount, double currentPrice) {
        try {
            double bidAmount = Double.parseDouble(inputAmount);

            // Kiểm tra logic cơ bản
            if (bidAmount <= currentPrice) {
                System.out.println("Lỗi: Số tiền đặt phải cao hơn giá hiện tại!");
                return;
            }

            // Mọi thứ hợp lệ -> Đóng gói thành Object (Sử dụng class Serializable của bạn)
            // BidRequest request = new BidRequest(username, productName, bidAmount);

            // Ở đây tôi dùng String tạm để demo, bạn thay bằng object BidRequest của bạn nhé
            String request = "BID|" + username + "|" + productName + "|" + bidAmount;

            // Gọi tầng Network để gửi đi
            AuctionroomClient networkClient = new AuctionroomClient(controller);
            networkClient.sendBidRequest(request);

        } catch (NumberFormatException e) {
            System.out.println("Lỗi: Vui lòng nhập số!");
        }
    }
}