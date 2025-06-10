package aid.controllers;
import aid.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginController {
    private final Stage stage;

    public LoginController(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        ImageView logo = new ImageView(new Image(getClass().getResource("/images/LOGOlogin.png").toExternalForm()));
        logo.setFitHeight(70); // atur tinggi sesuai kebutuhan
        logo.setPreserveRatio(true);

        TextField nickField = new TextField();
        nickField.setPromptText("Username");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(300);
        Label toRegisterLabel = new Label("Don't have an account? Sign up");
        toRegisterLabel.getStyleClass().add("link-label");
        toRegisterLabel.setOnMouseClicked(e -> {
            RegisterController registerController = new RegisterController(stage);
            registerController.show();
        });

        VBox container = new VBox(10, nickField, passField, loginBtn);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.getStyleClass().add("container");
        container.setMaxWidth(350);
        container.setMaxHeight(350);

        VBox root = new VBox(20, logo, container, toRegisterLabel); // label di bawah kotak putih
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("AID - Login");
        stage.show();
    }
}