package Auction.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Đường dẫn bắt đầu bằng dấu "/" trỏ thẳng vào thư mục resources
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // Khởi tạo Scene (khung cảnh chứa giao diện)
        Scene scene = new Scene(root);

        // Cài đặt cho cửa sổ (Stage)
        primaryStage.setTitle("Hệ thống Đấu giá trực tuyến - giao diện chính"); // Tiêu đề cửa sổ
        primaryStage.setScene(scene); // Gắn giao diện vào cửa sổ
        primaryStage.setResizable(false); // (Tùy chọn) Không cho người dùng kéo giãn cửa sổ
        primaryStage.show(); // Hiển thị cửa sổ lên
    }

    public static void main(String[] args) {
        launch(args); // Lệnh mồi của JavaFX
    }
}