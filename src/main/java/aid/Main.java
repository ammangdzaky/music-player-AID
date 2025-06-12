package aid;

import aid.controllers.ProfileController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        new ProfileController().show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}