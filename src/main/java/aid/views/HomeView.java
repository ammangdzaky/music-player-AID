package aid.views;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import aid.models.Song;

import java.io.InputStream;
import java.util.List;

public class HomeView {

    private Stage stage;
    private Scene scene;

    private ListView<Song> songListView;
    private ListView<String> genreListView;
    private Label currentSongTitleLabel;
    private Label currentSongArtistLabel;
    private ImageView albumArtImageView;
    private Button playPauseButton;
    private TextField searchField;
    private Button prevButton;
    private Button nextButton;
    private Button shuffleButton;
    private Button repeatButton;
    private Slider volumeSlider;

    // Komponen untuk pesan notifikasi
    private VBox messageBox;
    private Label messageLabel;

    // Komponen baru untuk progress bar dan label waktu
    private ProgressBar songProgressBar;
    private Label currentTimeLabel;
    private Label totalTimeLabel;

    // Komponen untuk tombol kecepatan
    private Button speed025xButton;
    private Button speed05xButton;
    private Button speed075xButton;
    private Button speed1xButton;
    private Button speed125xButton;
    private Button speed15xButton;
    private Button speed175xButton;
    private Button speed2xButton;

    // Tombol Home (Dideklarasikan di sini)
    private Button homeButton;
    private Button profileButton; // Deklarasi tombol profil


    // Callback untuk controller (akan diatur oleh HomeController)
    private SeekCallback seekCallback;


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

    public interface SeekCallback {
        void onSeek(double progress);
    }

    public void setSeekCallback(SeekCallback callback) {
        this.seekCallback = callback;
    }

    private void initializeUI() {
        BorderPane contentPane = new BorderPane();
        contentPane.getStyleClass().add("root-pane");
        contentPane.setStyle("-fx-border-color: " + BORDER_YELLOW + "; -fx-border-width: 3px; -fx-border-radius: " + BORDER_RADIUS_LG + ";");

        HBox header = createHeader();
        contentPane.setTop(header);

        VBox sidebar = createSidebar();
        contentPane.setLeft(sidebar);

        HBox mainContentArea = createMainContentArea();
        contentPane.setCenter(mainContentArea);

        VBox playerControls = createPlayerControls();
        contentPane.setBottom(playerControls);

        messageLabel = new Label();
        messageBox = new VBox(messageLabel);
        messageBox.getStyleClass().add("message-box");
        messageBox.setVisible(false);
        messageBox.setManaged(false);
        messageBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane();
        root.getChildren().addAll(contentPane, messageBox);
        StackPane.setAlignment(messageBox, Pos.TOP_CENTER);
        StackPane.setMargin(messageBox, new Insets(20, 0, 0, 0));

        this.scene = new Scene(root, 1024, 768);
        this.scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
        this.scene.getStylesheets().add(getClass().getResource("/styles/homeStyle.css").toExternalForm());

        stage.setFullScreenExitHint("");

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.setFullScreen(false);
                showMessage("Keluar dari mode Full Screen.", "info");
            }
        });
    }

    public Scene getScene() {
        return this.scene;
    }

    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setPadding(new Insets(10, 20, 10, 10));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header-pane");

        HBox leftNavIcons = new HBox(10);
        leftNavIcons.setAlignment(Pos.CENTER_LEFT);
        
        homeButton = createIconButton("🏠", FONT_SIZE_XLARGE, TEXT_LIGHT); 
        profileButton = createIconButton("👤", FONT_SIZE_XLARGE, TEXT_LIGHT);

        leftNavIcons.getChildren().addAll(
            homeButton,
            profileButton
        );
        leftNavIcons.getChildren().forEach(node -> {
            if (node instanceof Button) {
                node.getStyleClass().add("header-icon-button");
            }
        });

        Region spacerLeft = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);

        // --- PERUBAHAN DI SINI UNTUK LOGO HOME ---
        ImageView logoHomeImageView = null;
        try {
            // Memuat logoHome.png untuk posisi di lingkaran merah
            InputStream logoHomeStream = getClass().getResourceAsStream("/images/Logo2.png");
            if (logoHomeStream != null) {
                logoHomeImageView = new ImageView(new Image(logoHomeStream));
                logoHomeImageView.setFitWidth(150); // Sesuaikan ukuran sesuai kebutuhan
                logoHomeImageView.setFitHeight(50); // Sesuaikan ukuran sesuai kebutuhan
                logoHomeImageView.setPreserveRatio(true);
            } else {
                System.err.println("Warning: Logo2.png not found in resources.");
                // Fallback ke label teks jika gambar tidak ditemukan
                Label logoFallback = new Label("AID music");
                logoFallback.setStyle("-fx-font-family: 'Arial Black', sans-serif; -fx-font-size: 20px; -fx-text-fill: #FFD700;");
                logoHomeImageView = new ImageView(); // Bisa juga tambahkan logoFallback ke HBox
            }
        } catch (Exception e) {
            System.err.println("Error loading Logo2.png: " + e.getMessage());
            Label logoFallback = new Label("AID music");
            logoFallback.setStyle("-fx-font-family: 'Arial Black', sans-serif; -fx-font-size: 20px; -fx-text-fill: #FFD700;");
            logoHomeImageView = new ImageView();
        }
        // --- AKHIR PERUBAHAN LOGO HOME ---

        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.getStyleClass().add("search-field");

        Button searchButton = createIconButton("🔍", FONT_SIZE_LARGE, TEXT_LIGHT);
        searchButton.getStyleClass().add("search-button");

        Runnable performSearch = () -> {
            showMessage("Mencari: " + searchField.getText().trim(), "info");
            // Animasi shake hanya jika AnimataFx diimpor dan digunakan
            // new Shake(searchField).play(); 
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
        // --- PERUBAHAN SUSUNAN HEADER ---
        header.getChildren().addAll(leftNavIcons, spacerLeft, searchArea, logoHomeImageView, exitButton); // Memindahkan logoHomeImageView ke kanan
        // --- AKHIR PERUBAHAN SUSUNAN HEADER ---

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
        genreTitle.getStyleClass().add("genre-title"); // CSS akan mengatur warna ke kuning

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
            // new Tada(button).play(); // AnimataFx
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
        Label title = new Label("DAFTAR LAGU");
        title.getStyleClass().add("card-title"); // CSS akan mengatur warna ke kuning
        header.getChildren().add(title);
        header.setAlignment(Pos.CENTER);
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

            {
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
                        InputStream coverStream = getClass().getResourceAsStream("/images/" + song.getCover());
                        if (coverStream != null) {
                            songAlbumArt.setImage(new Image(coverStream));
                        } else {
                            System.err.println("Error loading cover art for " + song.getTitle() + ": /images/" + song.getCover() + " not found.");
                            songAlbumArt.setImage(new Image(getClass().getResourceAsStream("/images/default_album_art.png")));
                        }
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
        try {
            albumArtImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_album_art.png")));
        } catch (Exception e) {
            System.err.println("Error loading default_album_art.png: " + e.getMessage());
        }

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
            shuffleButton = createPlayerRoundButton("🔀"),
            prevButton = createPlayerRoundButton("⏮️"),
            playPauseButton = createPlayerRoundButton("▶️"),
            nextButton = createPlayerRoundButton("⏭️"),
            repeatButton = createPlayerRoundButton("🔁")
        );
        column.getChildren().add(playerControls);

        HBox volumeControl = new HBox(10);
        volumeControl.setAlignment(Pos.CENTER);
        Label volumeIcon = new Label("🔊");
        volumeIcon.setStyle("-fx-font-size: 20px; -fx-text-fill: " + TEXT_LIGHT + ";");
        volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setPrefWidth(200);
        volumeSlider.getStyleClass().add("volume-slider");
        volumeControl.getChildren().addAll(volumeIcon, volumeSlider);
        column.getChildren().add(volumeControl);

        return column;
    }

    private Button createPlayerRoundButton(String iconText) {
        Label iconLabel = new Label(iconText);
        iconLabel.setStyle("-fx-font-size: " + FONT_SIZE_LARGE + "; -fx-text-fill: " + ACCENT_YELLOW + ";");
        Button button = new Button();
        button.setGraphic(iconLabel);
        button.setPrefSize(40, 40);
        button.getStyleClass().add("player-round-button");
        return button;
    }

    public void updatePlayPauseButtonIcon(boolean isPlaying) {
        Label iconLabel;
        if (isPlaying) {
            iconLabel = new Label("⏸");
        } else {
            iconLabel = new Label("▶");
        }
        iconLabel.setStyle("-fx-font-size: " + FONT_SIZE_LARGE + "; -fx-text-fill: " + ACCENT_YELLOW + ";");
        playPauseButton.setGraphic(iconLabel);
    }

    private VBox createPlayerControls() {
        VBox playerArea = new VBox(10);
        playerArea.setPadding(new Insets(10, 20, 10, 20));
        playerArea.getStyleClass().add("bottom-player-area");

        songProgressBar = new ProgressBar(0.0);
        songProgressBar.setMaxWidth(Double.MAX_VALUE);
        songProgressBar.getStyleClass().add("song-progress-bar");

        songProgressBar.setOnMousePressed(e -> {
            if (seekCallback != null) {
                double progress = e.getX() / songProgressBar.getWidth();
                seekCallback.onSeek(progress);
            }
        });

        songProgressBar.setOnMouseDragged(e -> {
            if (seekCallback != null) {
                double progress = e.getX() / songProgressBar.getWidth();
                progress = Math.max(0.0, Math.min(1.0, progress));
                seekCallback.onSeek(progress);
            }
        });

        HBox timeLabels = new HBox();
        currentTimeLabel = new Label("0:00");
        currentTimeLabel.getStyleClass().add("time-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        totalTimeLabel = new Label("0:00");
        totalTimeLabel.getStyleClass().add("time-label");
        timeLabels.getChildren().addAll(currentTimeLabel, spacer, totalTimeLabel);

        HBox speedControlsContainer = new HBox(5);
        speedControlsContainer.setAlignment(Pos.CENTER);
        
        speed025xButton = createSpeedButton("0.25x");
        speed05xButton = createSpeedButton("0.5x");
        speed075xButton = createSpeedButton("0.75x");
        speed1xButton = createSpeedButton("1x");
        speed125xButton = createSpeedButton("1.25x");
        speed15xButton = createSpeedButton("1.5x");
        speed175xButton = createSpeedButton("1.75x");
        speed2xButton = createSpeedButton("2x");

        speedControlsContainer.getChildren().addAll(
            speed025xButton, speed05xButton, speed075xButton, speed1xButton,
            speed125xButton, speed15xButton, speed175xButton, speed2xButton
        );
        HBox.setHgrow(speedControlsContainer, Priority.ALWAYS); 
        VBox.setMargin(speedControlsContainer, new Insets(10, 0, 0, 0)); 

        playerArea.getChildren().addAll(songProgressBar, timeLabels, speedControlsContainer);
        return playerArea;
    }

    private Button createSpeedButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("speed-button");
        return button;
    }

    public void updateSpeedButtonVisual(double activeRate) {
        Platform.runLater(() -> {
            speed025xButton.getStyleClass().remove("speed-button-active");
            speed05xButton.getStyleClass().remove("speed-button-active");
            speed075xButton.getStyleClass().remove("speed-button-active");
            speed1xButton.getStyleClass().remove("speed-button-active");
            speed125xButton.getStyleClass().remove("speed-button-active");
            speed15xButton.getStyleClass().remove("speed-button-active");
            speed175xButton.getStyleClass().remove("speed-button-active");
            speed2xButton.getStyleClass().remove("speed-button-active");

            if (activeRate == 0.25) {
                speed025xButton.getStyleClass().add("speed-button-active");
            } else if (activeRate == 0.5) {
                speed05xButton.getStyleClass().add("speed-button-active");
            } else if (activeRate == 0.75) {
                speed075xButton.getStyleClass().add("speed-button-active");
            } else if (activeRate == 1.0) {
                speed1xButton.getStyleClass().add("speed-button-active");
            } else if (activeRate == 1.25) {
                speed125xButton.getStyleClass().add("speed-button-active");
            } else if (activeRate == 1.5) {
                speed15xButton.getStyleClass().add("speed-button-active");
            } else if (activeRate == 1.75) {
                speed175xButton.getStyleClass().add("speed-button-active");
            } else if (activeRate == 2.0) {
                speed2xButton.getStyleClass().add("speed-button-active");
            }
        });
    }

    public void updateProgressBar(double progress) {
        Platform.runLater(() -> songProgressBar.setProgress(progress));
    }

    public void updateCurrentTimeLabel(String time) {
        Platform.runLater(() -> currentTimeLabel.setText(time));
    }

    public void updateTotalTimeLabel(String time) {
        Platform.runLater(() -> totalTimeLabel.setText(time));
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
    public Button getPrevButton() {
        return prevButton;
    }

    public Button getNextButton() {
        return nextButton;
    }
    public Button getShuffleButton() {
        return shuffleButton;
    }

    public Button getRepeatButton() {
        return repeatButton;
    }

    public Slider getVolumeSlider() {
        return volumeSlider;
    }

    public Button getSpeed025xButton() { return speed025xButton; }
    public Button getSpeed05xButton() { return speed05xButton; }
    public Button getSpeed075xButton() { return speed075xButton; }
    public Button getSpeed1xButton() { return speed1xButton; }
    public Button getSpeed125xButton() { return speed125xButton; }
    public Button getSpeed15xButton() { return speed15xButton; }
    public Button getSpeed175xButton() { return speed175xButton; }
    public Button getSpeed2xButton() { return speed2xButton; }

    public Button getHomeButton() { return homeButton; }
    public Button getProfileButton() { return profileButton; }


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
                InputStream coverStream = getClass().getResourceAsStream("/images/" + song.getCover());
                if (coverStream != null) {
                    albumArtImageView.setImage(new Image(coverStream));
                } else {
                    System.err.println("Error loading album art for " + song.getTitle() + ": /images/" + song.getCover() + " not found.");
                    albumArtImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_album_art.png")));
                }
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

    public void updateShuffleButtonVisual(boolean isOn) {
        if (isOn) {
            shuffleButton.getStyleClass().add("shuffle-button-active");
        } else {
            shuffleButton.getStyleClass().remove("shuffle-button-active");
        }
    }

    public void updateRepeatButtonVisual(boolean isOn) {
        if (isOn) {
            repeatButton.getStyleClass().add("repeat-button-active");
        } else {
            repeatButton.getStyleClass().remove("repeat-button-active");
        }
    }

    public void showMessage(String message, String type) {
        messageLabel.setText(message);

        Platform.runLater(() -> {
            messageBox.setVisible(true);
            messageBox.setManaged(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), messageBox);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            PauseTransition delay = new PauseTransition(Duration.seconds(2.0));
            delay.setOnFinished(event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), messageBox);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    messageBox.setVisible(false);
                    messageBox.setManaged(false);
                });
                fadeOut.play();
            });
            delay.play();
        });
    }
}