package controller.autionroom;

import controller.login.changeScence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.AutionroomService;
import service.Product;

import java.io.IOException;

public class AuctionroomController {

    @FXML private Label username;
    @FXML private Label balance;
    @FXML private Label productname;
    @FXML private Label curentprice;
    @FXML private Label hightestbidder;
    @FXML private Label timeremaining;
    @FXML private TextField amount;

    private Product currentProduct;
    private String currentUser = "UET_Student";
    private double currentBalance = 15000000.0;

    // Khởi tạo Service để sử dụng
    private AutionroomService auctionService = new AutionroomService();

    public void initData(Product product) {
        this.currentProduct = product;
        productname.setText(product.getName());
        curentprice.setText(String.format("%.0f", product.getPrice()) + " VND");
        username.setText(currentUser);
        balance.setText(String.format("%.0f", currentBalance) + " VND");
        hightestbidder.setText("Chưa có");
        timeremaining.setText("15:00");
    }

    @FXML
    public void Bidded(ActionEvent event) {
        String inputAmount = amount.getText();

        // 1. Nhờ Service kiểm tra và xử lý logic
        String result = "SECCESS"; //auctionService.validateAndProcessBid(currentProduct, inputAmount, currentBalance);

        // 2. Dựa vào kết quả của Service để cập nhật Giao diện
        if (result.equals("SUCCESS")) {
            double bidAmount = Double.parseDouble(inputAmount);

            // Tạm thời cập nhật UI trực tiếp (Sau này Socket trả về mới cập nhật)
            currentProduct.setPrice(bidAmount);
            curentprice.setText(String.valueOf(bidAmount));
            hightestbidder.setText(currentUser);

            currentBalance -= bidAmount;
            balance.setText(String.valueOf(currentBalance));

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Bạn đã đặt giá thành công!");
            amount.clear();
        } else {
            // Nếu có lỗi, in câu thông báo lỗi từ Service ra
            showAlert(Alert.AlertType.WARNING, "Lỗi đặt giá", result);
        }
    }

    @FXML
    public void backScence(ActionEvent event) {
        changeScence.switchScene(event, "/fxml/home.fxml","Trang chủ đấu giá");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}