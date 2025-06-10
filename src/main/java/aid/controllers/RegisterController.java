package aid.controllers;
import aid.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

public class RegisterController {
    private final Stage stage;
    private Image profileImage;

    public RegisterController(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        ImageView logo = new ImageView(new Image(getClass().getResource("/images/LOGOsignup.png").toExternalForm()));
        logo.setFitHeight(70); // atur tinggi sesuai kebutuhan
        logo.setPreserveRatio(true);
        TextField nickField = new TextField();
        nickField.setPromptText("Username");
        TextField fullField = new TextField();
        fullField.setPromptText("Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        ImageView profileView = new ImageView();
        profileView.setFitWidth(100);
        profileView.setFitHeight(100);
        // Set gambar default avatar WA
        Image defaultAvatar = new Image(getClass().getResource("/images/default_avatar.jpg").toExternalForm());
        profileView.setImage(defaultAvatar);
        // Buat lingkaran untuk clip dan border
        Circle clip = new Circle(50, 50, 50); // x, y, radius (pastikan sesuai ukuran profileView)
        profileView.setClip(clip);
        // Tambahkan lingkaran border di atas profileView
        Circle border = new Circle(50, 50, 50);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.BLACK); // warna garis
        border.setStrokeWidth(3);      // ketebalan garis

        StackPane photoPane = new StackPane(profileView, border);
        photoPane.setPrefSize(100, 100);

        Button uploadBtn = new Button("Upload Profile");
        FileChooser fileChooser = new FileChooser();
        uploadBtn.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                profileImage = new Image(file.toURI().toString());
                double min = Math.min(profileImage.getWidth(), profileImage.getHeight());
                double x = (profileImage.getWidth() - min) / 2;
                double y = (profileImage.getHeight() - min) / 2;
                profileView.setImage(profileImage);
                profileView.setViewport(new javafx.geometry.Rectangle2D(x, y, min, min));
            }
        });

        Button submitBtn = new Button("Sign up");
        submitBtn.setMaxWidth(300);
        Label toLoginLabel = new Label("Already have account? Login");
        toLoginLabel.getStyleClass().add("link-label");
        toLoginLabel.setOnMouseClicked(e -> {
            LoginController loginController = new LoginController(stage);
            loginController.show();
        });

        submitBtn.setOnAction(e -> {
            String email = emailField.getText();
            // Regex sederhana untuk validasi email
            boolean emailValid = email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

            // Jika user tidak upload foto, pakai default
            Image finalProfileImage = (profileImage != null) ? profileImage : defaultAvatar;

            if (!nickField.getText().isEmpty() &&
            !fullField.getText().isEmpty() &&
            !email.isEmpty() &&
            emailValid) {
                User user = new User(nickField.getText(), fullField.getText(), finalProfileImage);
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

        VBox container = new VBox(10, nickField, fullField, emailField, passField, uploadBtn, photoPane, submitBtn, toLoginLabel);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.getStyleClass().add("container");
        container.setMaxWidth(350);
        container.setMaxHeight(350);

        VBox root = new VBox(20, logo, container); // logo di atas kotak putih
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("AID - Register");
        stage.show();
    }
}