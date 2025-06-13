package aid.controllers;

import aid.views.ProfileView;
import aid.models.User;
import javafx.stage.Stage;

public class ProfileController {
    private ProfileView profileView;
    private Stage stage;
    private User user;

    // Konstruktor baru
    public ProfileController(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        // ProfileView sekarang menerima Stage dan User juga
        this.profileView = new ProfileView(this, stage, user);
    }

    public void show() {
        stage.setScene(profileView.getScene());
        stage.setTitle("AID MUSIC - Profile"); // Ubah judul sesuai scene
        stage.setFullScreen(true);
        stage.show();
    }
    
    // Metode untuk kembali ke HomeScene (akan dipanggil dari ProfileView)
    public void goToHome(User user) {
        HomeController homeController = new HomeController(stage, user);
        homeController.show();
    }
}