package aid.views;

import aid.controllers.HomeController; // Import HomeController
import aid.controllers.ProfileController;
import aid.models.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import aid.utils.songUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ProfileView {
    private ProfileController controller;
    private Stage primaryStage;
    private Scene mainScene;
    private ImageView profileImageView;
    private Text userName;
    private Text nickName;
    private HBox playlistContainer = new HBox(10);
    private VBox rightBox = new VBox();
    private Text playlistCountText;
    private List<File> mySongFiles = new ArrayList<>();
    private FlowPane mySongsPane = new FlowPane(10, 10);
    private User user;

    public ProfileView(ProfileController controller, Stage primaryStage, User user) {
        this.controller = controller;
        this.primaryStage = primaryStage;
        this.user = user;

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // Logo - dengan error handling
        ImageView logoView = null;
        try {
            InputStream logoStream = getClass().getResourceAsStream("/images/Logo2.png");
            if (logoStream != null) {
                Image logoImage = new Image(logoStream);
                logoView = new ImageView(logoImage);
                logoView.setFitWidth(140);
                logoView.setFitHeight(70);
                logoView.setPreserveRatio(false);
                logoView.setSmooth(true);
            } else {
                // Fallback jika logo tidak ditemukan
                logoView = new ImageView();
                logoView.setFitWidth(140);
                logoView.setFitHeight(70);
                System.err.println("Warning: Logo2.png not found in resources.");
            }
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
            logoView = new ImageView();
            logoView.setFitWidth(140);
            logoView.setFitHeight(70);
        }

        StackPane logoPane = new StackPane(logoView);
        logoPane.setPadding(new Insets(20, 20, 10, 0));
        logoPane.setAlignment(Pos.CENTER);

        // HOME Button - dengan error handling
        Button homeButton = createIconButton("/images/iconHome.png", "Home");
        homeButton.setPadding(new Insets(10, 25, 0, 25));
        // --- PERUBAHAN DI SINI UNTUK NAVIGASI KE HOME ---
        homeButton.setOnAction(e -> {
            controller.goToHome(user); // Panggil metode di ProfileController untuk navigasi ke Home
        });
        // --- AKHIR PERUBAHAN ---

        // Setting Button - dengan error handling
        Button settingButton = createIconButton("/images/iconSetting.png", "Settings");
        settingButton.setPadding(new Insets(10, 25, 0, 25));
        settingButton.setOnAction(e -> showSettingScene());

        // Tambah Playlist Button - dengan error handling
        Button addPlaylistButton = createIconButton("/images/iconPlaylist.png", "Add Playlist");
        addPlaylistButton.setText("+");
        addPlaylistButton.getStyleClass().add("add-playlist-button");
        addPlaylistButton.setPadding(new Insets(10, 25, 0, 25));
        addPlaylistButton.setOnAction(e -> showAddPlaylistPopup());

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        HBox logoRow = new HBox(20);
        logoRow.setPadding(new Insets(20, 0, 10, 30));
        logoRow.setAlignment(Pos.CENTER);
        logoRow.setSpacing(20);
        logoRow.getChildren().addAll(homeButton, settingButton, addPlaylistButton, leftSpacer, logoPane);

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);

        // Profile Picture - dengan error handling
        profileImageView = new ImageView();
        try {
            if (user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty()) {
                // Jika path adalah file lokal
                File file = new File(user.getProfileImagePath());
                if (file.exists()) {
                    profileImageView.setImage(new Image(file.toURI().toString()));
                } else {
                    // fallback ke resource jika file tidak ada
                    InputStream profileStream = getClass().getResourceAsStream(user.getProfileImagePath());
                    if (profileStream != null) {
                        profileImageView.setImage(new Image(profileStream));
                    } else {
                        System.err.println("Warning: Profile image not found at " + user.getProfileImagePath());
                    }
                }
            } else {
                InputStream profileStream = getClass().getResourceAsStream("/images/indii.jpg");
                if (profileStream != null) {
                    profileImageView.setImage(new Image(profileStream));
                } else {
                    System.err.println("Warning: Default profile image /images/indii.jpg not found.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading profile image: " + e.getMessage());
        }

        profileImageView.setFitWidth(100);
        profileImageView.setFitHeight(100);
        profileImageView.setPreserveRatio(false);
        Circle circle = new Circle(50, 50, 50);
        profileImageView.setClip(circle);
        HBox.setMargin(profileImageView, new Insets(20, 0, 0, 20));

        // User Info
        userName = new Text(user.getUserName());
        userName.getStyleClass().add("user-name");

        nickName = new Text(user.getNickName());
        nickName.getStyleClass().add("nick-name");

        VBox userInfoBox = new VBox(userName, nickName);
        userInfoBox.setPadding(new Insets(20, 0, 0, 20));
        userInfoBox.setSpacing(5);

        // Stats Box
        Text followingCountText = new Text("2k");
        followingCountText.getStyleClass().add("stat-value");
        VBox followingBox = createStatBox("Following", followingCountText);

        Text followersCountText = new Text("291k");
        followersCountText.getStyleClass().add("stat-value");
        VBox followersBox = createStatBox("Followers", followersCountText);

        // playlist count
        playlistCountText = new Text("0");
        playlistCountText.getStyleClass().add("stat-value");
        VBox playlistBox = createStatBox("Playlists", playlistCountText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statsRow = new HBox(10, spacer, followingBox, followersBox, playlistBox);
        statsRow.setPadding(new Insets(20, 20, 0, 0));
        statsRow.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(statsRow, Priority.ALWAYS);

        HBox userSection = new HBox(userInfoBox, statsRow);
        userSection.setAlignment(Pos.CENTER_LEFT);
        userSection.setSpacing(20);

        VBox userBox = new VBox(userSection);
        HBox.setHgrow(userBox, Priority.ALWAYS);

        header.getChildren().addAll(profileImageView, userBox);

        VBox topBox = new VBox(logoRow, header);

        root.setTop(topBox);

        // konten utama
        VBox mainContentBox = new VBox();
        mainContentBox.getStyleClass().add("main-content");
        mainContentBox.setPadding(new Insets(20, 20, 20, 20));
        mainContentBox.setSpacing(20);

        Label myPlaylistLabel = new Label("My Playlists");
        myPlaylistLabel.getStyleClass().add("section-title");

        ScrollPane playlistScroll = new ScrollPane(playlistContainer);
        playlistScroll.setId("playlist-scroll"); // Pastikan ID sesuai dengan CSS
        playlistScroll.getStyleClass().add("scroll-pane"); // Tambahkan style class
        playlistScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        playlistScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        playlistScroll.setFitToHeight(true);
        playlistScroll.setFitToWidth(false);
        playlistScroll.setPrefHeight(150);
        playlistScroll.setPrefViewportHeight(160);
        playlistScroll.setPrefViewportWidth(900);

        // Pastikan playlist container juga transparan
        playlistContainer.getStyleClass().add("playlist-container");
        playlistContainer.setStyle("-fx-background-color: transparent;");

        // Alternative: Set background langsung di kode
        playlistScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Jika masih ada masalah, coba tambahkan ini ke mainContentBox:
        mainContentBox.setStyle("-fx-background-color: rgb(0, 0, 0);");

        Button mySongsButton = new Button("My Songs");
        mySongsButton.getStyleClass().add("my-songs-button");
        mainContentBox.getChildren().addAll(myPlaylistLabel, playlistScroll, mySongsButton, mySongsPane);

        // heandler button mySongs
        mySongsButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih Lagu");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.aac"));

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null && !mySongFiles.contains(file)) {
                mySongFiles.add(file);

                // tombol bulat untuk laguvv
                Button songButton = new Button("♪");
                songButton.getStyleClass().addAll("circle-btn", "play");
                songButton.setPrefSize(56, 56);
                songButton.setTooltip(new javafx.scene.control.Tooltip(file.getName()));

                songButton.setOnAction(ev -> showMySongPlayer(file));

                mySongsPane.getChildren().add(songButton);
                showMySongPlayer(file);
            }
        });

        HBox centerRow = new HBox();
        centerRow.setPadding(new Insets(20, 20, 20, 20));
        centerRow.setSpacing(0);
        HBox.setHgrow(mainContentBox, Priority.ALWAYS);
        centerRow.getChildren().add(mainContentBox);
        BorderPane.setMargin(centerRow, new Insets(0, 0, -10, -10));

        root.setCenter(centerRow);
        rightBox.getStyleClass().add("right-sidebar");
        rightBox.setPrefWidth(350);
        rightBox.setMaxWidth(350);
        rightBox.setMinWidth(350);
        BorderPane.setMargin(rightBox, new Insets(20, 10, 10, 0));
        BorderPane.setMargin(rightBox, new Insets(20, 10, 10, 0));

        root.setRight(rightBox);

        Scene scene = new Scene(root);
        // Error handling untuk CSS
        try {
            InputStream cssStream = getClass().getResourceAsStream("/styles/profileStyle.css");
            if (cssStream != null) {
                scene.getStylesheets().add(getClass().getResource("/styles/profileStyle.css").toExternalForm());
            } else {
                System.err.println("Warning: profileStyle.css not found.");
            }
        } catch (Exception e) {
            System.err.println("Error loading CSS: " + e.getMessage());
        }

        this.mainScene = scene;
        primaryStage.setTitle("AID MUSIC - Profile");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public Scene getScene() {
        return mainScene;
    }

    private Button createIconButton(String iconPath, String tooltipText) {
        Button button = new Button();
        try {
            InputStream iconStream = getClass().getResourceAsStream(iconPath);
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                ImageView imageView = new ImageView(icon);
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                button.setGraphic(imageView);
            } else {
                System.err.println("Warning: Icon " + iconPath + " not found.");
                button.setText(tooltipText); // Fallback ke text jika icon tidak ditemukan
            }
        } catch (Exception e) {
            System.err.println("Error loading icon " + iconPath + ": " + e.getMessage());
            button.setText(tooltipText); // Fallback ke text jika terjadi error
        }
        button.getStyleClass().add("icon-button");
        return button;
    }

    private void showSettingScene() {
        VBox settingLayout = new VBox(20);
        settingLayout.setPadding(new Insets(30));
        settingLayout.setAlignment(Pos.CENTER);
        settingLayout.getStyleClass().add("setting-layout");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField(userName.getText());
        usernameField.getStyleClass().add("text-field");

        Label nicknameLabel = new Label("Nickname:");
        TextField nicknameField = new TextField(nickName.getText());
        nicknameField.getStyleClass().add("text-field");

        // Tombol Ganti Foto Profil
        Button changePhotoButton = new Button("Ganti Foto Profil");
        changePhotoButton.getStyleClass().add("change-photo-button");
        final File[] selectedFile = { null };

        changePhotoButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih Gambar");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFile[0] = file;
                changePhotoButton.setText("Foto Dipilih: " + file.getName());
            }
        });

        // Tombol Simpan
        Button saveButton = new Button("Simpan");
        saveButton.getStyleClass().add("save-button");
        saveButton.setOnAction(e -> {
            String newUsername = usernameField.getText().trim();
            String newNickname = nicknameField.getText().trim();
            String oldUserName = user.getUserName(); // simpan sebelum diubah

            if (!newUsername.isEmpty()) {
                user.setUserName(newUsername);
                userName.setText(newUsername);
            }
            if (!newNickname.isEmpty()) {
                user.setNickName(newNickname);
                nickName.setText(newNickname);
            }
            if (selectedFile[0] != null && selectedFile[0].exists()) {
                user.setProfileImagePath(selectedFile[0].getAbsolutePath());
                profileImageView.setImage(new Image(selectedFile[0].toURI().toString()));
            }

            aid.utils.UserDataUtil.updateUser(oldUserName, user);

            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        });

        Button cancelButton = new Button("Batal");
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.setOnAction(e -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        settingLayout.getChildren().addAll(
                usernameLabel, usernameField,
                nicknameLabel, nicknameField,
                changePhotoButton,
                buttonBox);

        Scene settingScene = new Scene(settingLayout, 400, 450);
        settingScene.setFill(javafx.scene.paint.Color.TRANSPARENT); // Set transparent background

        // Error handling untuk CSS
        try {
            InputStream cssStream = getClass().getResourceAsStream("/styles/profileStyle.css");
            if (cssStream != null) {
                settingScene.getStylesheets().add(getClass().getResource("/styles/profileStyle.css").toExternalForm());
            } else {
                System.err.println("Warning: profileStyle.css not found for settings.");
            }
        } catch (Exception ex) {
            System.err.println("Error loading CSS for settings: " + ex.getMessage());
        }

        Stage popupStage = new Stage();
        popupStage.initOwner(primaryStage);
        popupStage.initModality(Modality.WINDOW_MODAL); // Tambahkan modality
        popupStage.setScene(settingScene);
        popupStage.setResizable(false);
        popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT); // Tambahkan style tanpa border
        popupStage.show();
    }

    private void showAddPlaylistPopup() {
        VBox popupLayout = new VBox(15);
        popupLayout.setPadding(new Insets(25));
        popupLayout.setAlignment(Pos.CENTER);
        popupLayout.getStyleClass().add("popup-layout");

        Label titleLabel = new Label("Tambah Playlist Baru");
        titleLabel.getStyleClass().add("popup-title");

        Label nameLabel = new Label("Nama Playlist:");
        TextField nameField = new TextField();
        nameField.setPromptText("Masukkan nama playlist");
        nameField.getStyleClass().add("popup-text-field");

        Label descLabel = new Label("Deskripsi:");
        TextField descField = new TextField();
        descField.setPromptText("Deskripsi playlist");
        descField.getStyleClass().add("popup-text-field");

        Label laguLabel = new Label("Pilih Lagu:");
        List<Song> songs = songUtils.loadSongsFromJson(getClass());
        ListView<Song> laguListView = new ListView<>(FXCollections.observableArrayList(songs));
        laguListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        laguListView.setPrefHeight(120); // Atur tinggi sesuai kebutuhan

        // Custom cell untuk tampilkan cover + judul + artist
        laguListView.setCellFactory(lv -> new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);
                if (empty || song == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // MENGGUNAKAN GETTER
                    setText(song.getTitle() + " - " + song.getArtist());
                    try {
                        // MODIFIKASI INI: Tambahkan prefiks "images/" di sini
                        InputStream coverStream = getClass().getResourceAsStream("/images/" + song.getCover());
                        if (coverStream != null) {
                            imageView.setImage(new Image(coverStream, 32, 32, true, true));
                        } else {
                            System.err.println("Cover image not found for song: " + song.getTitle() + " at /images/" + song.getCover());
                            imageView.setImage(null); // Atau set ke gambar placeholder
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading cover image for " + song.getTitle() + ": " + e.getMessage());
                        imageView.setImage(null); // Atau set ke gambar placeholder
                    }
                    imageView.setFitWidth(32);
                    imageView.setFitHeight(32);
                    setGraphic(imageView);
                }
            }
        });

        // Tombol Simpan dan Batal
        Button simpanBtn = new Button("Simpan");
        simpanBtn.getStyleClass().add("primary-button");
        simpanBtn.setOnAction(e -> {
            String playlistName = nameField.getText().trim();
            String playlistDesc = descField.getText().trim();
            ObservableList<Song> selectedSongs = laguListView.getSelectionModel().getSelectedItems();

            if (!playlistName.isEmpty() && !selectedSongs.isEmpty()) {
                addPlaylist(playlistName, playlistDesc, selectedSongs);
            }
            Stage stage = (Stage) simpanBtn.getScene().getWindow();
            stage.close();
        });

        Button batalBtn = new Button("Batal");
        batalBtn.getStyleClass().add("secondary-button");
        batalBtn.setOnAction(e -> {
            Stage stage = (Stage) batalBtn.getScene().getWindow();
            stage.close();
        });

        HBox buttonBox = new HBox(10, simpanBtn, batalBtn);
        buttonBox.setAlignment(Pos.CENTER);

        popupLayout.getChildren().addAll(
                titleLabel,
                nameLabel, nameField,
                descLabel, descField,
                laguLabel, laguListView,
                buttonBox);

        Scene popupScene = new Scene(popupLayout, 400, 400);

        // Error handling untuk CSS
        try {
            InputStream cssStream = getClass().getResourceAsStream("/styles/profileStyle.css");
            if (cssStream != null) {
                popupScene.getStylesheets().add(getClass().getResource("/styles/profileStyle.css").toExternalForm());
            } else {
                System.err.println("Warning: profileStyle.css not found for add playlist.");
            }
        } catch (Exception ex) {
            System.err.println("Error loading CSS for add playlist: " + ex.getMessage());
        }

        Stage popupStage = new Stage();
        popupStage.initOwner(primaryStage);
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.setTitle("Tambah Playlist");
        popupStage.setScene(popupScene);
        popupStage.setResizable(false);
        popupStage.show();
    }

    private VBox createStatBox(String label, Text valueText) {
        Text labelText = new Text(label);
        labelText.getStyleClass().add("stat-label");

        VBox box = new VBox(5, labelText, valueText);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(0, 10, 0, 10));
        return box;
    }

    private void showMySongPlayer(File audioFile) {
        VBox root = new VBox(24);
        root.getStyleClass().add("popup-player");
        root.setPrefWidth(350);
        root.setMaxWidth(350);
        root.setMinWidth(350);
        root.setPrefHeight(600);

        // Judul file
        Label title = new Label(audioFile.getName());
        title.getStyleClass().add("player-title");

        // Player bulat
        Button playCircle = new Button("▶");
        playCircle.getStyleClass().addAll("circle-btn", "play");
        playCircle.setStyle("-fx-shape: 'M50,10 a40,40 0 1,0 80,0 a40,40 0 1,0 -80,0';");

        // Progress bar
        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar(0);
        progressBar.getStyleClass().add("progress-bar");

        // Kontrol
        HBox controls = new HBox(24);
        controls.setAlignment(Pos.CENTER);
        Button pauseBtn = new Button("⏸");
        Button stopBtn = new Button("⏹");
        pauseBtn.getStyleClass().addAll("circle-btn");
        stopBtn.getStyleClass().addAll("circle-btn");
        controls.getChildren().addAll(playCircle, pauseBtn, stopBtn);

        // MediaPlayer
        MediaPlayer[] player = new MediaPlayer[1];

        playCircle.setOnAction(e -> {
            if (player[0] != null)
                player[0].stop();
            try {
                Media media = new Media(audioFile.toURI().toString());
                player[0] = new MediaPlayer(media);
                player[0].setVolume(1.0);
                player[0].currentTimeProperty().addListener((obs, oldVal, newVal) -> {
                    if (player[0].getTotalDuration() != null && player[0].getTotalDuration().toMillis() > 0) {
                        double progress = newVal.toMillis() / player[0].getTotalDuration().toMillis();
                        progressBar.setProgress(progress);
                    } else {
                        progressBar.setProgress(0);
                    }
                });
                player[0].play();
            } catch (Exception ex) {
                System.err.println("Gagal memutar file: " + ex.getMessage());
            }
        });

        pauseBtn.setOnAction(e -> {
            if (player[0] != null)
                player[0].pause();
        });
        stopBtn.setOnAction(e -> {
            if (player[0] != null)
                player[0].stop();
        });

        root.getChildren().addAll(title, progressBar, controls);
        rightBox.getChildren().setAll(root);
    }

    // Overloaded method to add playlist with a list of songs
    public void addPlaylist(String name, String description, ObservableList<Song> songs) {
        // MENGGUNAKAN GETTER
        String cover = (songs != null && !songs.isEmpty() && songs.get(0).getCover() != null)
                ? songs.get(0).getCover()
                : "default_cover.png"; // Hapus "images/" dari sini

        ImageView coverView = new ImageView();
        try {
            // MODIFIKASI INI: Tambahkan prefiks "images/" di sini
            InputStream coverStream = getClass().getResourceAsStream("/images/" + cover);
            if (coverStream != null) {
                coverView.setImage(new Image(coverStream));
            } else {
                System.err.println("Cover image not found for playlist: /images/" + cover);
            }
        } catch (Exception e) {
            System.err.println("Error loading cover image for playlist: " + e.getMessage());
            coverView.setImage(null);
        }
        coverView.setFitWidth(120);
        coverView.setFitHeight(120);
        coverView.setPreserveRatio(true);

        PlaylistCard card = new PlaylistCard(name, description, new java.util.ArrayList<>(songs), coverView);
        playlistContainer.getChildren().add(card);
        playlistCountText.setText(String.valueOf(playlistContainer.getChildren().size()));
    }

    private VBox showMusicPlayerPanel(Song song, List<Song> playlist) {
        VBox root = new VBox(24);
        root.getStyleClass().add("popup-player");
        root.setPrefWidth(350); // atau sesuai rightBox
        root.setMaxWidth(350);
        root.setMinWidth(350);
        root.setPrefHeight(600);

        // Header: Judul & Artis
        // MENGGUNAKAN GETTER
        Label title = new Label(song.getTitle());
        title.getStyleClass().add("player-title");
        Label artist = new Label(song.getArtist());
        artist.getStyleClass().add("player-artist");

        VBox info = new VBox(title, artist);
        info.setAlignment(Pos.CENTER);

        // --- TAMPILKAN DAFTAR LAGU DI PLAYLIST ---
        ListView<Song> songListView = new ListView<>(FXCollections.observableArrayList(playlist));
        songListView.getStyleClass().add("song-list-view");
        songListView.setPrefHeight(120);
        songListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                // MENGGUNAKAN GETTER
                setText((empty || item == null) ? null : item.getTitle() + " - " + item.getArtist());
            }
        });

        // Progress bar
        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar(0);
        progressBar.getStyleClass().add("progress-bar");

        // Kontrol musik
        HBox controls = new HBox(24);
        controls.setAlignment(Pos.CENTER);
        Button prevBtn = new Button("⏮");
        Button playBtn = new Button("▶");
        Button stopBtn = new Button("⏹");
        Button nextBtn = new Button("⏭");
        Button addBtn = new Button("+");
        prevBtn.getStyleClass().addAll("circle-btn");
        playBtn.getStyleClass().addAll("circle-btn", "play");
        stopBtn.getStyleClass().addAll("circle-btn");
        nextBtn.getStyleClass().addAll("circle-btn");
        addBtn.getStyleClass().addAll("circle-btn");

        controls.getChildren().addAll(prevBtn, playBtn, stopBtn, nextBtn);

        // Logic pemutaran lagu
        MediaPlayer[] player = new MediaPlayer[1];
        int[] songIndex = { playlist.indexOf(song) };

        Runnable[] playCurrent = new Runnable[1];
        playCurrent[0] = () -> {
            if (player[0] != null)
                player[0].stop();
            try {
                // MENGGUNAKAN GETTER
                String resource = getClass().getResource("/songs/" + playlist.get(songIndex[0]).getFile()).toExternalForm();
                Media media = new Media(resource);
                player[0] = new MediaPlayer(media);
                player[0].setVolume(1.0);
                player[0].setOnEndOfMedia(() -> {
                    if (songIndex[0] < playlist.size() - 1) {
                        songIndex[0]++;
                        playCurrent[0].run();
                        songListView.getSelectionModel().select(songIndex[0]);
                    }
                });
                // Update progress bar
                player[0].currentTimeProperty().addListener((obs, oldVal, newVal) -> {
                    if (player[0].getTotalDuration() != null && player[0].getTotalDuration().toMillis() > 0) {
                        double progress = newVal.toMillis() / player[0].getTotalDuration().toMillis();
                        progressBar.setProgress(progress);
                    } else {
                        progressBar.setProgress(0);
                    }
                });
                player[0].play();
                // MENGGUNAKAN GETTER
                title.setText(playlist.get(songIndex[0]).getTitle());
                artist.setText(playlist.get(songIndex[0]).getArtist());
                songListView.getSelectionModel().select(songIndex[0]);
            } catch (Exception e) {
                System.err.println("Gagal memutar lagu: " + e.getMessage());
            }
        };

        // Gunakan playCurrent[0].run() di semua tempat yang sebelumnya
        // playCurrent.run()
        playBtn.setOnAction(e -> playCurrent[0].run());
        stopBtn.setOnAction(e -> {
            if (player[0] != null)
                player[0].stop();
        });
        nextBtn.setOnAction(e -> {
            if (songIndex[0] < playlist.size() - 1) {
                songIndex[0]++;
                playCurrent[0].run();
            }
        });
        prevBtn.setOnAction(e -> {
            if (songIndex[0] > 0) {
                songIndex[0]--;
                playCurrent[0].run();
            }
        });

        // Tombol tambah lagu (bisa diisi logic sesuai kebutuhan)
        addBtn.setOnAction(e -> {
            List<Song> allSongs = songUtils.loadSongsFromJson(getClass());
            List<Song> notInPlaylist = allSongs.stream()
                    .filter(s -> !playlist.contains(s))
                    .toList();

            ListView<Song> pilihLagu = new ListView<>(FXCollections.observableArrayList(notInPlaylist));
            pilihLagu.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            pilihLagu.setPrefHeight(120);

            Label label = new Label("Pilih Lagu:");
            Button simpan = new Button("Simpan");
            simpan.setOnAction(ev -> {
                List<Song> selected = new java.util.ArrayList<>(pilihLagu.getSelectionModel().getSelectedItems());
                playlist.addAll(selected);
                songListView.getItems().addAll(selected);
                ((Stage) simpan.getScene().getWindow()).close();
            });

            VBox popupLayout = new VBox(16, label, pilihLagu, simpan);
            popupLayout.getStyleClass().add("add-song-popup");
            label.getStyleClass().add("label");
            pilihLagu.getStyleClass().add("list-view");
            simpan.getStyleClass().add("button");

            Scene popupScene = new Scene(popupLayout, 320, 280);
            try {
                InputStream cssStream = getClass().getResourceAsStream("/styles/profileStyle.css");
                if (cssStream != null) {
                    popupScene.getStylesheets().add(getClass().getResource("/styles/profileStyle.css").toExternalForm());
                } else {
                    System.err.println("Warning: profileStyle.css not found for add song popup.");
                }
            } catch (Exception ex) {
                System.err.println("Error loading CSS for add song popup: " + ex.getMessage());
            }
            Stage popupStage = new Stage();
            popupStage.initOwner(primaryStage);
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setScene(popupScene);
            popupStage.setTitle("Tambah Lagu ke Playlist");
            popupStage.setResizable(false);
            popupStage.show();
        });

        root.getChildren().addAll(info, songListView, progressBar, controls, addBtn);

        return root;
    }

    class PlaylistCard extends HBox {
        private List<Song> songs;
        private Label nameLabel;
        private Label descLabel;

        public PlaylistCard(String name, String description, List<Song> songs, ImageView coverView) {
            super(20);
            this.songs = songs;
            this.setPadding(new Insets(30));
            this.getStyleClass().add("playlist-card");
            this.setAlignment(Pos.CENTER_LEFT);

            VBox info = new VBox();
            nameLabel = new Label(name);
            nameLabel.getStyleClass().add("playlist-name");
            nameLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
            descLabel = new Label(description == null || description.isEmpty() ? "No description" : description);
            descLabel.getStyleClass().add("playlist-description");
            descLabel.setStyle("-fx-font-size: 18px;");
            info.getChildren().addAll(nameLabel, descLabel);
            info.setSpacing(10);

            this.getChildren().addAll(coverView, info);

            // Event klik pada card
            this.setOnMouseClicked(
                    e -> {
                        if (!this.getSongs().isEmpty()) {
                            rightBox.getChildren().setAll(showMusicPlayerPanel(this.getSongs().get(0), this.getSongs()));
                        } else {
                            System.out.println("Playlist is empty, cannot play.");
                            // Opsional: Tampilkan pesan ke pengguna atau panel kosong
                        }
                    });
        }

        public List<Song> getSongs() {
            return songs;
        }

        public void addSongs(List<Song> newSongs) {
            songs.addAll(newSongs);
        }
    }
}