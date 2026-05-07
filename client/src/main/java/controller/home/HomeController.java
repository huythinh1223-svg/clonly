package controller.home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import service.Product;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static service.MockDataServer.layDanhSachSanPhamTest;

public class HomeController implements Initializable {

    // Nối với cái VBox bạn đã đặt ID trong Scene Builder
    @FXML
    private VBox productListContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Giả sử lấy dữ liệu từ Server/Database
        List<Product> products = layDanhSachSanPhamTest();

        // Vòng lặp tự động tạo các Card và nhét vào cái Khung đã làm ở Scene Builder
        for (Product p : products) {
            VBox card = createProductCard(p);
            productListContainer.getChildren().add(card);
        }
    }

    // Tái sử dụng lại hàm tạo giao diện Card mà bạn đã tối ưu ở câu trước
    private VBox createProductCard(Product p) {
        VBox card = new VBox(8);
        card.setPadding(new javafx.geometry.Insets(15));

        // Bo góc, đổi màu viền và màu nền cho giống thẻ (card) thật
        card.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: #ffffff;");

        javafx.scene.control.Label name = new javafx.scene.control.Label(p.getName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        javafx.scene.control.Label price = new javafx.scene.control.Label("Giá hiện tại: " + p.getPrice());
        price.setStyle("-fx-text-fill: #d9534f; -fx-font-weight: bold;");

        javafx.scene.control.Button btn = new javafx.scene.control.Button("Vào đấu giá");
        btn.setStyle("-fx-background-color: #0275d8; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        card.getChildren().addAll(name, price, btn);

        // Dòng này chính là chìa khóa để chấm dứt lỗi đỏ ở cuối hàm
        return card;
    }
}
