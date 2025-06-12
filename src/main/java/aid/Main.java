package aid;

import javafx.application.Application;
import javafx.stage.Stage;

import aid.views.HomeView;
import aid.controllers.HomeController; // Import HomeController

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        HomeView homeView = new HomeView(primaryStage); // Buat HomeView
        HomeController homeController = new HomeController(homeView); // Buat HomeController dan inject HomeView

        // Panggil metode awal di controller untuk memuat data dan mengatur event handlers
        homeController.loadSongsAndGenres();
        homeController.loadInitialPlayerInfo();
        homeController.setupEventHandlers();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
