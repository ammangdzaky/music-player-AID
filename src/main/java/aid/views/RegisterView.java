package aid.views;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

public class RegisterView {
    public TextField nickField = new TextField();
    public TextField fullField = new TextField();
    public PasswordField passField = new PasswordField();
    public Button uploadBtn = new Button("Upload Profile");
    public Button submitBtn = new Button("Sign up");
    public Label toLoginLabel = new Label("Udah punya akun? Login");
    public ImageView profileView = new ImageView();
    public Image defaultAvatar;
    public StackPane photoPane;
    public VBox root;

    public RegisterView() {
        ImageView logo = new ImageView(new Image(getClass().getResource("/images/LOGOsignup.png").toExternalForm()));
        logo.setFitHeight(70);
        logo.setPreserveRatio(true);

        nickField.setPromptText("Username");
        fullField.setPromptText("Nickname");
        passField.setPromptText("Password");

        profileView.setFitWidth(100);
        profileView.setFitHeight(100);
        defaultAvatar = new Image(getClass().getResource("/images/default_avatar.jpg").toExternalForm());
        profileView.setImage(defaultAvatar);

        Circle clip = new Circle(50, 50, 50);
        profileView.setClip(clip);

        Circle border = new Circle(50, 50, 50);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.BLACK);
        border.setStrokeWidth(3);

        photoPane = new StackPane(profileView, border);
        photoPane.setPrefSize(100, 100);

        toLoginLabel.getStyleClass().add("link-label");

        VBox container = new VBox(8, photoPane, uploadBtn, nickField, fullField, passField, submitBtn);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.getStyleClass().add("container");
        container.setMaxWidth(350);
        container.setMaxHeight(350);

        root = new VBox(20, logo, container, toLoginLabel);
        root.setAlignment(Pos.CENTER);
    }

    public Scene getScene() {
        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/LOGstyle.css").toExternalForm());
        return scene;
    }
}