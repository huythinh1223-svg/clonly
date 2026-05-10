package network;

import javafx.application.Platform;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AuctionroomClient {

    private static final String SERVER_IP = "127.0.0.1"; // Chạy localhost
    private static final int SERVER_PORT = 8888;         // Port của Server

    // Truyền thêm Controller vào để sau khi mạng chạy xong có thể gọi hàm cập nhật UI
    private Object controller;

    public AuctionroomClient(Object controller) {
        this.controller = controller;
    }

    public void sendBidRequest(Object bidRequest) {
        // TẠO MỘT LUỒNG MỚI (THREAD) ĐỂ KHÔNG LÀM ĐƠ GIAO DIỆN
        new Thread(() -> {
            // Dùng try-with-resources để tự động đóng luồng sau khi dùng xong
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                // 1. Đẩy Object (BidRequest) lên Server
                oos.writeObject(bidRequest);
                oos.flush();
                System.out.println("Đã gửi request đấu giá lên Server...");

                // 2. Chờ Server xử lý và trả kết quả về (Thread sẽ tạm dừng ở đây cho đến khi có phản hồi)
                Object response = ois.readObject();

                // 3. Có kết quả từ Server! CẬP NHẬT GIAO DIỆN
                // Bắt buộc phải bọc trong Platform.runLater để đẩy lệnh về lại luồng UI của JavaFX
                Platform.runLater(() -> {
                    // Giả sử response trả về là String "SUCCESS" hoặc object BidResponse
                    System.out.println("Server trả về: " + response.toString());

                    // Tại đây, bạn có thể ép kiểu controller và gọi hàm cập nhật giao diện
                    // ((AuctionroomController) controller).updateUIAfterBidding(response);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    System.out.println("Lỗi kết nối đến Server!");
                });
            }
        }).start(); // Bắt đầu chạy luồng
    }
}