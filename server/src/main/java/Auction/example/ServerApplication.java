package Auction.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApplication {
    private static final int PORT = 8888; // Cổng kết nối

    public static void main(String[] args) {
        System.out.println("Server đang khởi động và lắng nghe tại port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Chờ Client kết nối tới
                Socket clientSocket = serverSocket.accept();
                System.out.println("Có client mới kết nối: " + clientSocket.getInetAddress());

                // Đẩy việc xử lý Client này vào một Thread riêng (để Server có thể phục vụ nhiều người cùng lúc)
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Class xử lý yêu cầu của từng Client
class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Đọc tin nhắn từ Client
            String request = in.readLine();
            System.out.println("Nhận được từ Client: " + request);

            if (request != null && request.startsWith("LOGIN")) {
                // Tách chuỗi "LOGIN|username|password"
                String[] parts = request.split("\\|");
                if (parts.length == 3) {
                    String username = parts[1];
                    String password = parts[2];

                    // XỬ LÝ LOGIC ĐĂNG NHẬP (Tạm thời hardcode để test)
                    if (username.equals("admin") && password.equals("123456")) {
                        out.println("SUCCESS"); // Gửi phản hồi thành công
                    } else {
                        out.println("FAIL");    // Gửi phản hồi thất bại
                    }
                } else {
                    out.println("ERROR|Sai định dạng tin nhắn");
                }
            }
        } catch (Exception e) {
            System.out.println("Kết nối với Client bị ngắt.");
        }
    }
}
