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
        this.profileView = new ProfileView(this, stage, user);
    }

    public void show() {
        stage.setScene(profileView.getScene());
        stage.setTitle("AID MUSIC");
        stage.setFullScreen(true);
        stage.show();
    }
}