package aid.controllers;

import aid.models.User;
import aid.views.RegisterView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import java.io.File;

public class RegisterController {
    private final Stage stage;
    private final RegisterView view;
    private Image profileImage;

    public RegisterController(Stage stage) {
        this.stage = stage;
        this.view = new RegisterView();
        initialize();
    }

    private void initialize() {
        FileChooser fileChooser = new FileChooser();
        view.uploadBtn.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                profileImage = new Image(file.toURI().toString());
                double min = Math.min(profileImage.getWidth(), profileImage.getHeight());
                double x = (profileImage.getWidth() - min) / 2;
                double y = (profileImage.getHeight() - min) / 2;
                view.profileView.setImage(profileImage);
                view.profileView.setViewport(new javafx.geometry.Rectangle2D(x, y, min, min));
            }
        });

        view.toLoginLabel.setOnMouseClicked(e -> {
            LoginController loginController = new LoginController(stage);
            loginController.show();
        });

        view.submitBtn.setOnAction(e -> {
            String email = view.emailField.getText();
            boolean emailValid = email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
            Image finalProfileImage = (profileImage != null) ? profileImage : view.defaultAvatar;

            if (!view.nickField.getText().isEmpty() &&
                !view.fullField.getText().isEmpty() &&
                !email.isEmpty() &&
                emailValid) {
                User user = new User(view.nickField.getText(), view.fullField.getText(), finalProfileImage);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration successful");
                alert.showAndWait();
                LoginController loginController = new LoginController(stage);
                loginController.show();
            } else if (!emailValid) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email tidak valid!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Semua field harus diisi!");
                alert.showAndWait();
            }
        });
    }

    public void show() {
        stage.setScene(view.getScene());
        stage.setTitle("AID - Sign up");
        stage.show();
    }
}