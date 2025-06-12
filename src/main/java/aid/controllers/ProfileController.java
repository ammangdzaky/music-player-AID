package aid.controllers;

import aid.views.ProfileView;
import javafx.stage.Stage;

public class ProfileController {
    private ProfileView profileView;

    public void show(Stage stage) {
        profileView = new ProfileView(this, stage);
        stage.setScene(profileView.getScene());
        stage.setTitle("AID MUSIC");
        stage.setFullScreen(true);
        stage.show();
    }
}