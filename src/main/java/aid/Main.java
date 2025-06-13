package aid;

import aid.controllers.LoginController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginController loginController = new LoginController(primaryStage);
        loginController.show();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }
}