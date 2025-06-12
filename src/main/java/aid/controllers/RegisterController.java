package aid.controllers;
import aid.models.User;
import aid.views.RegisterView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import java.io.File;
import aid.utils.UserDataUtil;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RegisterController {
    private final Stage stage;
    private final RegisterView view;
    private Image profileImage;
    private String profileExt = null; // Simpan ekstensi file upload

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
                try {
                    // 1. Buat folder tujuan jika belum ada
                    String destDir = "src/main/resources/images/fotoprofileUser";
                    Files.createDirectories(Paths.get(destDir));
                    // 2. Ambil ekstensi file
                    profileExt = file.getName().substring(file.getName().lastIndexOf('.'));
                    // 3. Nama file baru (pakai username)
                    String newFileName = view.nickField.getText() + profileExt;
                    Path destPath = Paths.get(destDir, newFileName);
                    // 4. Copy file ke folder tujuan
                    Files.copy(file.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                    // 5. Set profileImage dari file hasil copy
                    profileImage = new Image(destPath.toUri().toString());
                    double min = Math.min(profileImage.getWidth(), profileImage.getHeight());
                    double x = (profileImage.getWidth() - min) / 2;
                    double y = (profileImage.getHeight() - min) / 2;
                    view.profileView.setImage(profileImage);
                    view.profileView.setViewport(new javafx.geometry.Rectangle2D(x, y, min, min));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal upload foto profil!");
                    alert.showAndWait();
                }
            }
        });

        view.toLoginLabel.setOnMouseClicked(e -> {
            LoginController loginController = new LoginController(stage);
            loginController.show();
        });

        view.submitBtn.setOnAction(e -> {
            if (UserDataUtil.isUsernameTaken(view.nickField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Username sudah dipakai!");
                alert.showAndWait();
                return;
            }

            if (!view.nickField.getText().isEmpty() && !view.fullField.getText().isEmpty()) {
                String profilePath;
                if (profileImage != null && profileExt != null) {
                    // Simpan path relatif ke folder resources, sesuai ekstensi upload
                    profilePath = "/images/fotoprofileUser/" + view.nickField.getText() + profileExt;
                } else {
                    profilePath = "/images/default_avatar.jpg";
                }
                User user = new User(
                    view.nickField.getText(),
                    view.fullField.getText(),
                    view.passField.getText(),
                    profilePath
                );
                UserDataUtil.addUser(user);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration successful");
                alert.showAndWait();
                LoginController loginController = new LoginController(stage);
                loginController.show();
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