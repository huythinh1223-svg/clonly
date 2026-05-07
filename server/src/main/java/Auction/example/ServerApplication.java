package Auction.example;

import dao.DatabaseManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApplication {
    private static final int PORT = 8888; // Cổng kết nối

    public static void main(String[] args) {
        // Tích hợp database : Khởi tạo và nạp dữ liệu cũ từ ổ cứng lên RAM
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.loadDataOnStartup();

        //  Bắt sự kiện tắt máy chủ để tự động lưu dữ liệu an toàn
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[CẢNH BÁO] Phát hiện tín hiệu tắt máy chủ!");
            dbManager.saveAllData(); // Lưu toàn bộ Auction và User xuống file .dat
            System.out.println("Đã lưu dữ liệu thành công. Tạm biệt!");
        }));
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
            String request;

            // Vòng lặp lắng nghe Client
            while ((request = in.readLine()) != null) {
                System.out.println("Nhận được từ Client: " + request);

                if (request.startsWith("LOGIN")) {
                    handleLogin(request, out);
                }
                else if (request.equals("GET_AUCTIONS")) {
                    out.println("AUCTION_LIST|Tai nghe Sony-5500000|Bàn phím cơ-2150000");
                }
                else {
                    out.println("ERROR|Lệnh không hợp lệ");
                }
            }
        } catch (Exception e) {
            System.out.println("Kết nối với Client bị ngắt.");
        }
    }

    //Hàm xử lý logic Đăng nhập
    private void handleLogin(String request, PrintWriter out) {
        String[] parts = request.split("\\|");
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];

            // CẢI TIẾN 3: Đã XÓA hardcode. Dùng UserManager để đối chiếu với Database thật
            boolean isValidUser = Auction.example.model.user.UserManager.getInstance().authenticate(username, password);

            if (isValidUser) {
                out.println("SUCCESS"); // Gửi phản hồi thành công
            } else {
                out.println("FAIL");    // Gửi phản hồi thất bại (Sai tài khoản/mật khẩu)
            }
        } else {
            out.println("ERROR|Sai định dạng tin nhắn LOGIN");
        }
    }

    private void handleGetAuctions(PrintWriter out) {
        // Tạm thời trả về chuỗi text để Client in ra giao diện.
        out.println("AUCTION_LIST|Tai nghe Sony-5500000|Bàn phím cơ-2150000");
    }
}
