package aid;

import javafx.application.Application;
import javafx.stage.Stage;

import aid.views.HomeView;
import aid.controllers.HomeController; // Import HomeController

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Buat instance HomeView
        HomeView homeView = new HomeView(primaryStage);
        
        // Buat instance HomeController dan injeksikan HomeView ke dalamnya
        HomeController homeController = new HomeController(homeView);

        // Panggil metode-metode di HomeController untuk memuat data dan mengatur event handlers
        homeController.loadSongsAndGenres();     // Muat lagu dan genre ke HomeView
        homeController.loadInitialPlayerInfo();  // Muat info lagu pertama ke panel player
        homeController.setupEventHandlers();     // Atur event handlers (misal klik lagu, tombol play/pause)
    }

    public static void main(String[] args) {
        launch(args);
    }
}
