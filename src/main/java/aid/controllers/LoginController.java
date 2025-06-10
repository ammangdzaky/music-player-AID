package aid.controllers;
import aid.views.LoginView;
import javafx.stage.Stage;
import aid.models.User;
import aid.utils.UserDataUtil;

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
                view.toRegisterLabel.setText("Akun telah masuk!"); // Ganti label, bukan popup
            } else {
                view.toRegisterLabel.setText("Username/password salah!");
            }
        });
    }

    public void show() {
        stage.setScene(view.getScene());
        stage.setTitle("AID - Login");
        stage.show();
    }
}