package aid.views;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority; // Import Priority

public class LoginView {
    public TextField nickField = new TextField();
    public PasswordField passField = new PasswordField();
    public Button loginBtn = new Button("Login");
    public Label loginMessageLabel = new Label(); // label pesan login
    public Label toRegisterLabel = new Label("Gak punya akun? Sign up");
    public VBox root;

    public LoginView() {
        ImageView logo = new ImageView(new Image(getClass().getResource("/images/LOGOlogin.png").toExternalForm()));
        logo.setFitHeight(70);
        logo.setPreserveRatio(true);

        nickField.setPromptText("Username");
        passField.setPromptText("Password");
        loginBtn.setMaxWidth(300); // Batasi lebar tombol
        loginBtn.setPrefHeight(40); // Beri tinggi preferensi

        loginMessageLabel.setText("");
        loginMessageLabel.setWrapText(true);
        loginMessageLabel.setAlignment(Pos.CENTER);

        toRegisterLabel.getStyleClass().add("link-label");

        VBox container = new VBox(10, nickField, passField, loginBtn, loginMessageLabel);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.getStyleClass().add("container");
        container.setMaxWidth(350);
        // container.setMaxHeight(350); // <-- KOMENTARI BARIS INI (jika ada)

        root = new VBox(20); // Spasi antara elemen utama
        root.getChildren().addAll(logo, container, toRegisterLabel);
        root.setAlignment(Pos.CENTER);
        // root.setPadding(new Insets(50)); // Opsional: Tambahkan padding di root jika perlu ruang di sekitar elemen
        VBox.setVgrow(container, Priority.NEVER); // Pastikan container tidak terlalu 'tumbuh'

        // Pastikan root bisa menangkap event jika ada masalah z-index, meskipun jarang dibutuhkan
        root.setPickOnBounds(true);
    }

    public Scene getScene() {
        // Scene scene = new Scene(root, 400, 300); // Hapus ukuran tetap ini
        Scene scene = new Scene(root); // Biarkan Scene beradaptasi dengan ukuran root
        scene.getStylesheets().add(getClass().getResource("/styles/LOGstyle.css").toExternalForm());
        return scene;
    }
}