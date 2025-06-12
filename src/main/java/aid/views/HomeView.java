package aid.views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

import org.controlsfx.control.Notifications;
import animatefx.animation.*;

import aid.models.Song;

import java.util.List;

public class HomeView {

    private Stage stage;

    private ListView<Song> songListView;
    private ListView<String> genreListView;
    private Label currentSongTitleLabel;
    private Label currentSongArtistLabel;
    private ImageView albumArtImageView;
    private Button playPauseButton;
    private TextField searchField;
    // Tambahkan field untuk tombol Previous dan Next
    private Button prevButton;
    private Button nextButton;


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

    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setPadding(new Insets(10, 20, 10, 10));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header-pane");

        HBox leftNavIcons = new HBox(10);
        leftNavIcons.setAlignment(Pos.CENTER_LEFT);
        leftNavIcons.getChildren().addAll(
            createIconButton("🏠", FONT_SIZE_XLARGE, TEXT_LIGHT), // Home
            createIconButton("👤", FONT_SIZE_XLARGE, TEXT_LIGHT)  // Profile
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
            appLogoImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.jpg")));
            appLogoImageView.setFitWidth(200);
            appLogoImageView.setFitHeight(60);
            appLogoImageView.setPreserveRatio(true);
            appLogoImageView.getStyleClass().add("app-logo-image");
        } catch (Exception e) {
            System.err.println("Error loading logo.jpg: " + e.getMessage());
            Label logoFallback = new Label("AID music");
            logoFallback.setStyle("-fx-font-family: 'Arial Black', sans-serif; -fx-font-size: 20px; -fx-text-fill: #FFD700;");
            appLogoImageView = new ImageView();
        }
        
        searchField = new TextField();
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

        genreListView = new ListView<>();
        genreListView.getStyleClass().add("genre-list");
        genreListView.setPrefHeight(200);

        sidebar.getChildren().addAll(genreTitle, genreListView);
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

        songListView = new ListView<>();
        songListView.getStyleClass().add("song-list");
        songListView.setCellFactory(param -> new javafx.scene.control.ListCell<Song>() {
            private final HBox cellLayout = new HBox(10);
            private final ImageView songAlbumArt = new ImageView();
            private final VBox textInfo = new VBox(2);
            private final Label titleLabel = new Label();
            private final Label artistDurationLabel = new Label();
            private final Label albumLabel = new Label();

            { // Inisialisasi awal untuk setiap cell
                songAlbumArt.setFitWidth(40);
                songAlbumArt.setFitHeight(40);
                songAlbumArt.setClip(new Circle(20, 20, 20));

                titleLabel.getStyleClass().add("song-title-list");
                artistDurationLabel.getStyleClass().add("song-artist-duration-list");
                albumLabel.getStyleClass().add("song-album-list");
                albumLabel.setMaxWidth(150);
                albumLabel.setWrapText(true);

                textInfo.getChildren().addAll(titleLabel, artistDurationLabel);
                
                cellLayout.setAlignment(Pos.CENTER_LEFT);
                cellLayout.setPadding(new Insets(5, 0, 5, 0));
                HBox.setHgrow(textInfo, Priority.ALWAYS);
                
                cellLayout.getChildren().addAll(songAlbumArt, textInfo, albumLabel);
            }

            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);
                if (empty || song == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    titleLabel.setText(song.getTitle());
                    artistDurationLabel.setText(song.getArtist() + " • " + formatDuration(song.getDurationSeconds()));
                    albumLabel.setText(song.getAlbum());

                    try {
                        songAlbumArt.setImage(new Image(getClass().getResourceAsStream("/images/" + song.getCover())));
                    } catch (Exception e) {
                        System.err.println("Error loading cover art for " + song.getTitle() + ": " + e.getMessage());
                        songAlbumArt.setImage(new Image(getClass().getResourceAsStream("/images/default_album_art.png")));
                    }
                    setGraphic(cellLayout);
                }
            }
        });

        column.getChildren().add(songListView);
        return column;
    }

    private VBox createAlbumAndPlayerDetailColumn() {
        VBox column = new VBox(20);
        column.getStyleClass().addAll("content-card", "album-detail-card");
        column.setPadding(new Insets(20));

        VBox albumDetail = new VBox(10);
        albumDetail.setAlignment(Pos.CENTER);
        albumDetail.setPadding(new Insets(0, 0, 20, 0));

        albumArtImageView = new ImageView();
        albumArtImageView.setFitWidth(260);
        albumArtImageView.setFitHeight(260);
        albumArtImageView.getStyleClass().add("album-art");
        albumArtImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_album_art.png")));

        currentSongTitleLabel = new Label("No Song Playing");
        currentSongTitleLabel.getStyleClass().add("album-title");

        currentSongArtistLabel = new Label("");
        currentSongArtistLabel.getStyleClass().add("album-artist-duration");

        albumDetail.getChildren().addAll(albumArtImageView, currentSongTitleLabel, currentSongArtistLabel);
        column.getChildren().add(albumDetail);

        HBox playerControls = new HBox(15);
        playerControls.setAlignment(Pos.CENTER);
        playerControls.setPadding(new Insets(10));
        playerControls.getChildren().addAll(
            createPlayerRoundButton("🔀"),
            // Tetapkan tombol Previous dan Next ke field
            prevButton = createPlayerRoundButton("⏮️"),
            playPauseButton = createPlayerRoundButton("▶️"),
            nextButton = createPlayerRoundButton("⏭️"),
            createPlayerRoundButton("🔁")
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
            // Notifications.create().title("Player").text("Tombol " + iconText + " diklik!").showInformation();
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
            createPlayerRoundButton("🔀"),
            createPlayerRoundButton("⏮️"),
            createPlayerRoundButton("▶️"),
            createPlayerRoundButton("⏭️"),
            createPlayerRoundButton("🔁")
        );

        playerArea.getChildren().addAll(songProgressBar, timeLabels, centerControls);
        return playerArea;
    }

    private String formatDuration(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    public ListView<Song> getSongListView() {
        return songListView;
    }

    public ListView<String> getGenreListView() {
        return genreListView;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public Button getPlayPauseButton() {
        return playPauseButton;
    }
    // Tambahkan getter untuk tombol Previous dan Next
    public Button getPrevButton() {
        return prevButton;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public void displaySongs(List<Song> songs) {
        songListView.getItems().setAll(songs);
    }

    public void displayGenres(List<String> genres) {
        genreListView.getItems().setAll(genres);
    }

    public void updateCurrentSongInfo(Song song) {
        if (song != null) {
            currentSongTitleLabel.setText(song.getTitle());
            currentSongArtistLabel.setText(song.getArtist());
            try {
                albumArtImageView.setImage(new Image(getClass().getResourceAsStream("/images/" + song.getCover())));
            } catch (Exception e) {
                System.err.println("Error loading album art for " + song.getTitle() + ": " + e.getMessage());
                albumArtImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_album_art.png")));
            }
        } else {
            currentSongTitleLabel.setText("No Song Playing");
            currentSongArtistLabel.setText("");
            albumArtImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_album_art.png")));
        }
    }

    public String getFontSizeLarge() {
        return FONT_SIZE_LARGE;
    }

    public String getBgPrimaryDark() {
        return BG_PRIMARY_DARK;
    }
}