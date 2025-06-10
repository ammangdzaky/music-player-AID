package aid.controllers;
import aid.views.LoginView;
import javafx.stage.Stage;

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
            // validasi trus pindah ke home/utama
        });
    }

    public void show() {
        stage.setScene(view.getScene());
        stage.setTitle("AID - Login");
        stage.show();
    }
}