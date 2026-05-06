package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LogIn {

    @FXML
    private Button buttonLogin;

    @FXML
    private TextField passworld;

    @FXML
    private TextField username;

    @FXML
    private Label wrongLogin;

    @FXML
    void userLogin(ActionEvent event) {
        // 1. Lấy dữ liệu người dùng nhập
        String user = username.getText();
        String pass = passworld.getText();

        // 2. Validate (Kiểm tra) cơ bản ngay tại Client để giảm tải cho Server
        if (user.trim().isEmpty() || pass.trim().isEmpty()) {
            wrongLogin.setText("Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            wrongLogin.setStyle("-fx-text-fill: red;"); // Đổi màu chữ thành đỏ
            return; // Dừng hàm lại ngay lập tức, không gửi lên Server nữa
        }

        // 3. Báo hiệu cho người dùng biết đang xử lý
        wrongLogin.setText("Đang kết nối tới máy chủ...");
        wrongLogin.setStyle("-fx-text-fill: blue;"); // Đổi sang màu xanh

        // 4. Đóng gói và gửi xuống tầng Mạng (Network)
        /*
         * LƯU Ý: Phần này là code gợi ý để bạn hình dung luồng đi của dữ liệu.
         * Tùy thuộc vào cách bạn viết class xử lý Network (Socket/REST),
         * bạn sẽ thay thế đoạn comment này bằng hàm gọi Network thực tế.
         */

        // Ví dụ: Tạo đối tượng DTO từ module auction-common
        // LoginRequestDTO requestDTO = new LoginRequestDTO(user, pass);

        // Truyền cho class Network gửi đi và đợi phản hồi...
        // NetworkManager.sendLoginRequest(requestDTO, response -> {
        //
        //     // QUAN TRỌNG: Mọi thay đổi giao diện từ kết quả của luồng mạng
        //     // ĐỀU PHẢI được bọc trong Platform.runLater
        //     Platform.runLater(() -> {
        //         if (response.isSuccess()) {
        //             wrongLogin.setText("Đăng nhập thành công! Đang vào phòng đấu giá...");
        //             wrongLogin.setStyle("-fx-text-fill: green;");
        //             // TODO: Viết hàm chuyển Scene sang giao diện màn hình chính ở đây
        //         } else {
        //             wrongLogin.setText("Sai tài khoản hoặc mật khẩu!");
        //             wrongLogin.setStyle("-fx-text-fill: red;");
        //         }
        //     });
        // });

        // Tạm thời in ra Console để bạn test xem nút bấm đã hoạt động chưa
        wrongLogin.setText("Đang kết nối Server...");

        // Chạy một Thread ngầm để không làm đơ giao diện
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 8888);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // 1. Gửi request dạng chuỗi
                String request = "LOGIN|" + user + "|" + pass;
                out.println(request);

                // 2. Chờ đọc phản hồi từ Server
                String response = in.readLine();

                // 3. Cập nhật lại giao diện dựa trên kết quả (BẮT BUỘC dùng Platform.runLater)
                Platform.runLater(() -> {
                    if ("SUCCESS".equals(response)) {
                       chuyenSangTrangChu(event);
                    } else if ("FAIL".equals(response)) {
                        wrongLogin.setText("Tài khoản hoặc mật khẩu không chính xác!");
                    } else {
                        wrongLogin.setText("Lỗi từ server: " + response);
                    }
                });

            } catch (Exception e) {
                // Nếu không bật Server hoặc mất mạng
                Platform.runLater(() -> wrongLogin.setText("Không thể kết nối tới Server!"));
                e.printStackTrace();
            }
        }).start();
    }
    private void chuyenSangTrangChu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent homeRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(homeRoot));
            stage.setTitle("Trang chủ Đấu giá");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            wrongLogin.setText("Lỗi tải giao diện trang chủ!");
        }
    }
}
