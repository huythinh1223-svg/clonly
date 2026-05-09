package controller.login;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import service.LoginService;

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
    private Button buttonSignup;

    private final LoginService loginService = new LoginService();

    @FXML
    void userLogin(ActionEvent event) {

        String user = username.getText();
        String pass = passworld.getText();

        if (user.trim().isEmpty() || pass.trim().isEmpty()) {
            wrongLogin.setText(
                    "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        wrongLogin.setText("Đang kết nối tới server...");

        buttonLogin.setDisable(true);

        Thread thread = new Thread(() -> {
            try {

                String response = loginService.login(user, pass);

                Platform.runLater(() -> {
                    handleLoginResponse(response, event);
                    buttonLogin.setDisable(false);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    wrongLogin.setText("Không thể kết nối tới server!");
                    buttonLogin.setDisable(false);
                });
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void handleLoginResponse(String response, ActionEvent event) {
        switch (response) {
            case "SUCCESS":
                changeScence.switchScene(event, "/fxml/home.fxml", "Trang chủ đấu giá");
                break;

            case "FAIL":
                wrongLogin.setText("Sai tài khoản hoặc mật khẩu!");
                break;

            default:
                wrongLogin.setText("Lỗi từ server!");
        }
    }

    @FXML
    void userSignup(ActionEvent event) {
        changeScence.switchScene(event, "/fxml/signup.fxml", "Đăng ký tài khoản" );
    }
}