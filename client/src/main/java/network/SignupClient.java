package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SignupClient {

    public String createAccount(String full, String user, String mail, String pass) throws Exception {
        // Dùng try-with-resources để tự động đóng kết nối
        try (Socket socket = new Socket("localhost", 8888);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Server yêu cầu format: REGISTER|role|username|password|fullname|email|balance
            // Mặc định đăng ký mới là BIDDER với số dư 0.0
            String request = "REGISTER|BIDDER|" + user + "|" + pass + "|" + full + "|" + mail + "|0.0";

            out.println(request); // Gửi String lên Server

            return in.readLine(); // Đọc String Server trả về
        }
    }
}