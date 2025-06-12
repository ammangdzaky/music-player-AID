package aid.views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.controlsfx.control.Notifications;
import animatefx.animation.*;

public class HomeView {

    private Stage stage;

    // --- Konstanta Warna dan Styling ---
    private static final String BG_PRIMARY_DARK = "#000000";
    private static final String ACCENT_YELLOW = "#FFD700";
    private static final String BG_CARD_DARK = "#1a1a1a";
    private static final String TEXT_LIGHT = "#FFFFFF";
    private static final String TEXT_MEDIUM_GRAY = "#AAAAAA";
    private static final String BORDER_YELLOW = ACCENT_YELLOW;

    private static final String FONT_SIZE_SMALL = "12px";
    private static final String FONT_SIZE_MEDIUM = "14px";
    private static final String FONT_SIZE_LARGE = "18px";
    private static final String FONT_SIZE_XLARGE = "24px";

    private static final String BORDER_RADIUS_SM = "5px";
    private static final String BORDER_RADIUS_MD = "10px";
    private static final String BORDER_RADIUS_LG = "15px";

    public HomeView(Stage stage) {
        this.stage = stage;
        initializeUI();
    }

    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");
        root.setStyle("-fx-border-color: " + BORDER_YELLOW + "; -fx-border-width: 3px; -fx-border-radius: " + BORDER_RADIUS_LG + ";");

        HBox header = createHeader();
        root.setTop(header);

        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        HBox mainContentArea = createMainContentArea();
        root.setCenter(mainContentArea);

        VBox playerControls = createPlayerControls();
        root.setBottom(playerControls);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/homeStyle.css").toExternalForm());

        stage.setTitle("AID Music Player - Home");
        stage.setScene(scene);
        
        stage.setFullScreen(true); 
        stage.setFullScreenExitHint(""); 

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.setFullScreen(false);
                Notifications.create().title("Full Screen").text("Keluar dari mode Full Screen.").showInformation();
            }
        });

        stage.show();
    }

    // --- Bagian Header (Atas) ---
    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setPadding(new Insets(10, 20, 10, 10));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header-pane");

        // --- Grup Ikon Navigasi Kiri (Home, Profile) ---
        HBox leftNavIcons = new HBox(10);
        leftNavIcons.setAlignment(Pos.CENTER_LEFT);
        leftNavIcons.getChildren().addAll(
            createIconButton("🏠", FONT_SIZE_XLARGE, TEXT_LIGHT), // Home
            createIconButton("👤", FONT_SIZE_XLARGE, TEXT_LIGHT)  // Profile (Tetap gunakan 👤, masalahnya ada di font sistem)
        );
        leftNavIcons.getChildren().forEach(node -> {
            if (node instanceof Button) {
                node.getStyleClass().add("header-icon-button");
            }
        });

        Region spacerLeft = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);

        ImageView appLogoImageView = null;
        try {
            // --- KOREKSI UKURAN LOGO DI SINI (Ditingkatkan Lebih Lanjut) ---
            appLogoImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.jpg")));
            appLogoImageView.setFitWidth(190); 
            appLogoImageView.setFitHeight(50); 
            // Jika masih terlalu kecil/buram, coba:
            // appLogoImageView.setFitWidth(250);
            // appLogoImageView.setFitHeight(75);
            // Dan pastikan gambar logo.jpg asli Anda memiliki resolusi yang cukup tinggi
            // --- AKHIR KOREKSI ---
            appLogoImageView.setPreserveRatio(true);
            appLogoImageView.getStyleClass().add("app-logo-image");
        } catch (Exception e) {
            System.err.println("Error loading logo.jpg: " + e.getMessage());
            Label logoFallback = new Label("AID music");
            logoFallback.setStyle("-fx-font-family: 'Arial Black', sans-serif; -fx-font-size: 20px; -fx-text-fill: #FFD700;");
            appLogoImageView = new ImageView();
        }
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.getStyleClass().add("search-field");

        Button searchButton = createIconButton("🔍", FONT_SIZE_LARGE, TEXT_LIGHT);
        searchButton.getStyleClass().add("search-button");

        Runnable performSearch = () -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty()) {
                Notifications.create().title("Pencarian").text("Mencari: " + searchText).showWarning();
                new Shake(searchField).play();
            } else {
                Notifications.create().title("Pencarian").text("Kolom pencarian kosong.").showError();
            }
        };
        searchButton.setOnAction(e -> performSearch.run());
        searchField.setOnAction(e -> performSearch.run());

        HBox searchArea = new HBox(5);
        searchArea.setAlignment(Pos.CENTER_RIGHT);
        searchArea.getChildren().addAll(searchField, searchButton);

        Button exitButton = createIconButton("✖", FONT_SIZE_LARGE, TEXT_LIGHT);
        exitButton.getStyleClass().add("exit-button");
        exitButton.setOnAction(e -> Platform.exit());

        header.getChildren().clear();
        if (appLogoImageView != null) {
             header.getChildren().addAll(leftNavIcons, spacerLeft, appLogoImageView, searchArea, exitButton);
        } else {
             header.getChildren().addAll(leftNavIcons, spacerLeft, searchArea, exitButton);
        }
       
        return header;
    }

    private Button createIconButton(String iconText, String size, String color) {
        Label iconLabel = new Label(iconText);
        iconLabel.setStyle("-fx-font-size: " + size + "; -fx-text-fill: " + color + ";");
        Button button = new Button();
        button.setGraphic(iconLabel);
        button.setPadding(new Insets(5));
        button.setStyle("-fx-background-color: transparent; -fx-background-radius: 50%;");
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-background-color: #555555;"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle() + "-fx-background-color: transparent;"));
        return button;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20, 10, 20, 20));
        sidebar.setPrefWidth(200);
        sidebar.getStyleClass().add("sidebar");

        Label genreTitle = new Label("Genre");
        genreTitle.getStyleClass().add("genre-title");

        String[] genres = {"Rock", "Jazz", "POP", "Instrume", "POP Punk", "KPOP", "R&B"};
        for (String genre : genres) {
            Button genreBtn = createGenreButton(genre);
            sidebar.getChildren().add(genreBtn);
        }

        return sidebar;
    }

    private Button createGenreButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(160);
        button.setPrefHeight(35);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add("genre-button");
        
        if (text.equals("POP")) {
            button.getStyleClass().add("genre-button-active");
            new Tada(button).play();
        }
        return button;
    }

    private HBox createMainContentArea() {
        HBox mainContentArea = new HBox(20);
        mainContentArea.setPadding(new Insets(20));
        mainContentArea.getStyleClass().add("main-content-area");

        VBox similarSongsCol = createSimilarSongsColumn();
        HBox.setHgrow(similarSongsCol, Priority.ALWAYS);

        VBox albumAndPlayerDetail = createAlbumAndPlayerDetailColumn();
        albumAndPlayerDetail.setPrefWidth(300);

        mainContentArea.getChildren().addAll(similarSongsCol, albumAndPlayerDetail);
        return mainContentArea;
    }

    private VBox createSimilarSongsColumn() {
        VBox column = new VBox(15);
        column.getStyleClass().addAll("content-card", "similar-songs-card");
        column.setPadding(new Insets(20));

        HBox header = new HBox();
        Label title = new Label("Lagu Serupa");
        title.getStyleClass().add("card-title");
        header.getChildren().add(title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 10, 0));
        
        column.getChildren().add(header);

        GridPane songListGrid = new GridPane();
        songListGrid.setHgap(10);
        songListGrid.setVgap(10);

        String[] songTitles = {"Kota", "Senja", "Harapan", "Mimpi", "Pergi"};
        String[] artistDurations = {"Dere Cillian • 3:29", "Band A • 4:10", "Penyanyi B • 2:55", "Grup C • 3:40", "Solois D • 4:05"};
        String[] albumTitles = {"Berlayar", "Kekal", "Abadi", "Jaya", "Mind"};

        for (int i = 0; i < songTitles.length; i++) {
            addSongItemToGrid(songListGrid, i, songTitles[i], artistDurations[i], albumTitles[i]);
        }

        column.getChildren().add(songListGrid);
        return column;
    }

    private void addSongItemToGrid(GridPane grid, int rowIndex, String songTitle, String artistDuration, String albumTitle) {
        StackPane thumbnail = new StackPane();
        thumbnail.setPrefSize(50, 50);
        thumbnail.getStyleClass().add("song-thumbnail");
        Label thumbIcon = new Label("🎵");
        thumbIcon.setStyle("-fx-font-size: 24px; -fx-text-fill: " + TEXT_MEDIUM_GRAY + ";");
        thumbnail.getChildren().add(thumbIcon);
        
        VBox songInfo = new VBox(2);
        Label title = new Label(songTitle);
        title.getStyleClass().add("song-title");
        Label artist = new Label(artistDuration);
        artist.getStyleClass().add("song-artist");
        songInfo.getChildren().addAll(title, artist);

        Label album = new Label(albumTitle);
        album.getStyleClass().add("song-album");

        HBox rowContainer = new HBox(10);
        rowContainer.setAlignment(Pos.CENTER_LEFT);
        rowContainer.setPadding(new Insets(5));
        rowContainer.getStyleClass().add("song-list-item");
        
        rowContainer.getChildren().addAll(thumbnail, songInfo);
        HBox.setHgrow(songInfo, Priority.ALWAYS);
        rowContainer.getChildren().add(album);

        grid.add(rowContainer, 0, rowIndex);
        GridPane.setHgrow(rowContainer, Priority.ALWAYS);
    }

    private VBox createAlbumAndPlayerDetailColumn() {
        VBox column = new VBox(20);
        column.getStyleClass().addAll("content-card", "album-detail-card");
        column.setPadding(new Insets(20));

        VBox albumDetail = new VBox(10);
        albumDetail.setAlignment(Pos.CENTER);
        albumDetail.setPadding(new Insets(0, 0, 20, 0));

        Rectangle albumArt = new Rectangle(260, 260);
        albumArt.getStyleClass().add("album-art");

        Label albumTitle = new Label("Kota");
        albumTitle.getStyleClass().add("album-title");

        Label albumArtistDuration = new Label("Dere Cillian • 3:29");
        albumArtistDuration.getStyleClass().add("album-artist-duration");

        albumDetail.getChildren().addAll(albumArt, albumTitle, albumArtistDuration);
        column.getChildren().add(albumDetail);

        HBox playerControls = new HBox(15);
        playerControls.setAlignment(Pos.CENTER);
        playerControls.setPadding(new Insets(10));
        playerControls.getChildren().addAll(
            createPlayerRoundButton("🔀"), // Shuffle (Unicode)
            createPlayerRoundButton("⏮️"), // Previous (Unicode)
            createPlayerRoundButton("▶️"), // Play (Unicode)
            createPlayerRoundButton("⏭️"), // Next (Unicode)
            createPlayerRoundButton("🔁")  // Repeat (Unicode)
        );
        column.getChildren().add(playerControls);

        HBox volumeControl = new HBox(10);
        volumeControl.setAlignment(Pos.CENTER);
        Label volumeIcon = new Label("🔊");
        volumeIcon.setStyle("-fx-font-size: 20px; -fx-text-fill: " + TEXT_LIGHT + ";");
        Slider volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setPrefWidth(200);
        volumeSlider.getStyleClass().add("volume-slider");
        volumeControl.getChildren().addAll(volumeIcon, volumeSlider);
        column.getChildren().add(volumeControl);

        return column;
    }

    private Button createPlayerRoundButton(String iconText) {
        Label iconLabel = new Label(iconText);
        iconLabel.setStyle("-fx-font-size: " + FONT_SIZE_LARGE + "; -fx-text-fill: " + BG_PRIMARY_DARK + ";");
        Button button = new Button();
        button.setGraphic(iconLabel);
        button.setPrefSize(40, 40);
        button.getStyleClass().add("player-round-button");
        button.setOnAction(e -> {
            new Pulse(button).play();
            Notifications.create().title("Player").text("Tombol " + iconText + " diklik!").showInformation();
        });
        return button;
    }

    private VBox createPlayerControls() {
        VBox playerArea = new VBox(10);
        playerArea.setPadding(new Insets(10, 20, 10, 20));
        playerArea.getStyleClass().add("bottom-player-area");

        ProgressBar songProgressBar = new ProgressBar(0.5);
        songProgressBar.setPrefWidth(1000);
        songProgressBar.getStyleClass().add("song-progress-bar");
        
        HBox timeLabels = new HBox();
        Label currentTime = new Label("0:00");
        currentTime.getStyleClass().add("time-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label totalTime = new Label("3:29");
        totalTime.getStyleClass().add("time-label");
        timeLabels.getChildren().addAll(currentTime, spacer, totalTime);

        HBox centerControls = new HBox(20);
        centerControls.setAlignment(Pos.CENTER);
        centerControls.getChildren().addAll(
            createPlayerRoundButton("🔀"), // Shuffle
            createPlayerRoundButton("⏮️"), // Previous
            createPlayerRoundButton("▶️"), // Play
            createPlayerRoundButton("⏭️"), // Next
            createPlayerRoundButton("🔁")  // Repeat
        );

        playerArea.getChildren().addAll(songProgressBar, timeLabels, centerControls);
        return playerArea;
    }
}
