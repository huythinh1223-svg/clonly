package controller.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class changeScence {
    /**
     * Phương thức chuyển trang dùng chung thông qua ActionEvent (khi click nút)
     *
     * @param event    Sự kiện click chuột (để lấy được Stage hiện tại)
     * @param fxmlPath Đường dẫn tới file FXML cần load
     * @param title    Tiêu đề của cửa sổ mới
     */
    public static void switchScene(ActionEvent event, String fxmlPath, String title) {
        // Lấy Stage (cửa sổ) hiện tại từ ActionEvent
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        switchSceneByStage(stage, fxmlPath, title);
    }

    /**
     * Phương thức chuyển trang linh hoạt hơn, truyền trực tiếp Stage.
     * Dùng cho những trường hợp không có ActionEvent (ví dụ: chuyển trang từ MenuBar,
     * hoặc tự động chuyển sau một khoảng thời gian).
     *
     * @param stage    Stage hiện tại
     * @param fxmlPath Đường dẫn tới file FXML cần load
     * @param title    Tiêu đề của cửa sổ mới
     */
    public static void switchSceneByStage(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(changeScence.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Lỗi tải giao diện: " + fxmlPath);
            e.printStackTrace();
        }
    }
}

