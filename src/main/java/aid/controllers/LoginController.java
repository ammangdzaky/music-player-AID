package aid.controllers;
import aid.views.LoginView;
import javafx.stage.Stage;
import aid.models.User;
import aid.utils.UserDataUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

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
                // Buat label link untuk ke halaman utama
                Label linkToHome = new Label("Akun telah masuk! (klik)");
                linkToHome.getStyleClass().add("link-label");
                linkToHome.setOnMouseClicked(ev -> {
                    // TODO: Pindah ke halaman utama
                    // Contoh: new MainController(stage).show();
                });
                view.loginMessageLabel.setText(""); // reset text
                view.loginMessageLabel.setGraphic(linkToHome);
            } else {
                view.loginMessageLabel.setGraphic(null);
                view.loginMessageLabel.setText("Username/password salah!");
            }
        });
    }

    public void show() {
        stage.setScene(view.getScene());
        stage.setTitle("AID - Login");
        stage.show();
    }
}