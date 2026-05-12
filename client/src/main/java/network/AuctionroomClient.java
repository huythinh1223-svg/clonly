package network;

import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AuctionroomClient {

    private static final String SERVER_IP = "127.0.0.1"; // Chạy localhost
    private static final int SERVER_PORT = 8888;         // Port của Server

    // Truyền thêm Controller vào để sau khi mạng chạy xong có thể gọi hàm cập nhật UI
    private Object controller;

    public AuctionroomClient(Object controller) {
        this.controller = controller;
    }

    public void sendBidRequest(String request) {
        new Thread(() -> {
            // Dùng try-with-resources để tự động đóng luồng sau khi dùng xong
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Gửi request dạng String lên Server
                out.println(request);
                System.out.println("Đã gửi request đấu giá lên Server: " + request);

                // Chờ Server xử lý
                String response = in.readLine();

                Platform.runLater(() -> {
                    System.out.println("Server trả về: " + response);
                    // TODO: Ép kiểu controller và cập nhật UI dựa trên chữ SUCCESS hoặc FAIL
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