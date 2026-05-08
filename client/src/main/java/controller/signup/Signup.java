package controller.signup;

import controller.login.changeScence;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import service.AuthService;

public class Signup {

    @FXML
    private TextField fullName;

    @FXML
    private TextField userName;

    @FXML
    private TextField Email;

    @FXML
    private TextField Passworld;

    @FXML
    private Label wrongSignup;

    @FXML
    private Button buttonSignup;

    private final AuthService authService =
            new AuthService();

    @FXML
    private void createAccount(ActionEvent event) {

        String full = fullName.getText();
        String user = userName.getText();
        String mail = Email.getText();
        String pass = Passworld.getText();

        // validate cơ bản
        if(full.isEmpty() || user.isEmpty()
                || mail.isEmpty() || pass.isEmpty()) {

            wrongSignup.setText(
                    "Vui lòng nhập đầy đủ thông tin!"
            );

            return;
        }

        buttonSignup.setDisable(true);

        Thread thread = new Thread(() -> {

            try {

                String response =
                        authService.createAccount(
                                full,
                                user,
                                mail,
                                pass
                        );

                Platform.runLater(() -> {

                    handleResponse(response, event);

                    buttonSignup.setDisable(false);
                });

            } catch (Exception e) {

                e.printStackTrace();

                Platform.runLater(() -> {

                    wrongSignup.setText(
                            "Không thể kết nối tới server!"
                    );

                    buttonSignup.setDisable(false);
                });
            }
        });

        thread.setDaemon(true);

        thread.start();
    }

    private void handleResponse(
            String response,
            ActionEvent event
    ) {

        switch (response) {

            case "SUCCESS":

                wrongSignup.setText(
                        "Tạo tài khoản thành công."
                );

                changeScence.switchScene(
                        event,
                        "/fxml/login.fxml",
                        "Trang đăng nhập"
                );

                break;

            case "USERNAME_EXISTS":

                wrongSignup.setText(
                        "Tên người dùng đã tồn tại!"
                );

                break;

            default:

                wrongSignup.setText(
                        "Tạo tài khoản thất bại."
                );
        }
    }
}