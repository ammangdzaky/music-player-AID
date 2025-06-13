package aid.controllers;

import aid.views.LoginView;
import javafx.stage.Stage;
import aid.models.User;
import aid.utils.UserDataUtil;
// import javafx.geometry.Pos; // Tidak perlu diimport jika tidak digunakan
// import javafx.scene.control.Label; // Tidak perlu diimport jika tidak digunakan

public class LoginController {
    private final Stage stage;
    private final LoginView view;

    public LoginController(Stage stage) {
        this.stage = stage;
        this.view = new LoginView();
        initialize();
    }

    private void initialize() {
        view.toRegisterLabel.setOnMouseClicked(e -> {
            RegisterController registerController = new RegisterController(stage);
            registerController.show();
        });

        view.loginBtn.setOnAction(e -> {
            String nick = view.nickField.getText();
            String pass = view.passField.getText();
            User user = UserDataUtil.findUser(nick, pass);
            if (user != null) {
                // Login berhasil, pindah ke HomeScene
                // Meneruskan stage dan objek User yang berhasil login ke HomeController
                HomeController homeController = new HomeController(stage, user);
                homeController.show();
            } else {
                view.loginMessageLabel.setGraphic(null);
                view.loginMessageLabel.setText("Username/password salah!");
            }
        });
    }

    public void show() {
        stage.setScene(view.getScene());
        stage.setTitle("AID - Login");
        // Fullscreen sudah diatur di Main.java, tidak perlu di sini lagi kecuali ada kebutuhan spesifik
        // stage.setFullScreen(true); 
        stage.show();
    }
}