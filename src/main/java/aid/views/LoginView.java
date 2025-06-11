package aid.views;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class LoginView {
    public TextField nickField = new TextField();
    public PasswordField passField = new PasswordField();
    public Button loginBtn = new Button("Login");
    public Label toRegisterLabel = new Label("Don't have an account? Sign up");
    public VBox root;

    public LoginView() {
        ImageView logo = new ImageView(new Image(getClass().getResource("/images/LOGOlogin.png").toExternalForm()));
        logo.setFitHeight(70);
        logo.setPreserveRatio(true);

        nickField.setPromptText("Username");
        passField.setPromptText("Password");
        loginBtn.setMaxWidth(300);
        toRegisterLabel.getStyleClass().add("link-label");

        VBox container = new VBox(10, nickField, passField, loginBtn);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.getStyleClass().add("container");
        container.setMaxWidth(350);
        container.setMaxHeight(350);

        root = new VBox(20, logo, container, toRegisterLabel);
        root.setAlignment(Pos.CENTER);
    }

    public Scene getScene() {
        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles/LOGstyle.css").toExternalForm());
        return scene;
    }
}