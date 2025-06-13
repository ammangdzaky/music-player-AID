package aid.views;

import aid.controllers.HomeController;
import aid.controllers.ProfileController;
import aid.models.*;
import aid.utils.IDGenerator;
import aid.utils.UserDataUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
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
import java.util.stream.Collectors;

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

    private MediaPlayer currentProfileMediaPlayer;

    public ProfileView(ProfileController controller, Stage primaryStage, User user) {
        this.controller = controller;
        this.primaryStage = primaryStage;
        this.user = user;

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

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

        Button homeButton = createIconButton("/images/iconHome.png", "Home");
        homeButton.setPadding(new Insets(10, 25, 0, 25));
        homeButton.setOnAction(e -> {
            System.out.println("DEBUG: Home button clicked from Profile. Navigating to Home Scene.");
            stopAndDisposeProfileMediaPlayer();
            controller.goToHome(user);
        });

        Button settingButton = createIconButton("/images/iconSetting.png", "Settings");
        settingButton.setPadding(new Insets(10, 25, 0, 25));
        settingButton.setOnAction(e -> showSettingScene());

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

        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);

        profileImageView = new ImageView();
        try {
            if (user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty()) {
                File file = new File(user.getProfileImagePath());
                if (file.exists()) {
                    profileImageView.setImage(new Image(file.toURI().toString()));
                } else {
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

        userName = new Text(user.getUserName());
        userName.getStyleClass().add("user-name");

        nickName = new Text(user.getNickName());
        nickName.getStyleClass().add("nick-name");

        VBox userInfoBox = new VBox(userName, nickName);
        userInfoBox.setPadding(new Insets(20, 0, 0, 20));
        userInfoBox.setSpacing(5);

        Text followingCountText = new Text("2k");
        followingCountText.getStyleClass().add("stat-value");
        VBox followingBox = createStatBox("Following", followingCountText);

        Text followersCountText = new Text("291k");
        followersCountText.getStyleClass().add("stat-value");
        VBox followersBox = createStatBox("Followers", followersCountText);

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

        VBox mainContentBox = new VBox();
        mainContentBox.getStyleClass().add("main-content");
        mainContentBox.setPadding(new Insets(20, 20, 20, 20));
        mainContentBox.setSpacing(20);

        Label myPlaylistLabel = new Label("My Playlists");
        myPlaylistLabel.getStyleClass().add("section-title");

        ScrollPane playlistScroll = new ScrollPane(playlistContainer);
        playlistScroll.setId("playlist-scroll");
        playlistScroll.getStyleClass().add("scroll-pane");
        playlistScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        playlistScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        playlistScroll.setFitToHeight(true);
        playlistScroll.setFitToWidth(false);
        playlistScroll.setPrefHeight(150);
        playlistScroll.setPrefViewportHeight(160);
        playlistScroll.setPrefViewportWidth(900);

        playlistContainer.getStyleClass().add("playlist-container");
        playlistContainer.setStyle("-fx-background-color: transparent;");

        playlistScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        mainContentBox.setStyle("-fx-background-color: rgb(0, 0, 0);");

        Button mySongsButton = new Button("My Songs");
        mySongsButton.getStyleClass().add("my-songs-button");
        mainContentBox.getChildren().addAll(myPlaylistLabel, playlistScroll, mySongsButton, mySongsPane);

        mySongsButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih Lagu");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.aac"));

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null && !mySongFiles.contains(file)) {
                mySongFiles.add(file);

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

        loadUserPlaylists();
    }

    public Scene getScene() {
        return mainScene;
    }
    
    private void loadUserPlaylists() {
        playlistContainer.getChildren().clear();
        List<Playlist> userPlaylists = user.getPlaylists();
        System.out.println("DEBUG: Loading " + userPlaylists.size() + " playlists for user: " + user.getUserName());
        for (Playlist p : userPlaylists) {
            List<Song> songsInPlaylist = UserDataUtil.getDataManager().getSongsByIds(p.getSongIds(), p);
            
            String playlistCoverPath = "default_album_art.png";
            String playlistDescription = "Total songs: " + songsInPlaylist.size();
            
            if (!songsInPlaylist.isEmpty()) {
                playlistCoverPath = songsInPlaylist.get(0).getCover();
            }

            if (p instanceof SmartPlaylist) {
                SmartPlaylist sp = (SmartPlaylist) p;
                playlistDescription = "Smart Playlist (Genre: " + sp.getGenreCriteria() + ")";
                if (songsInPlaylist.isEmpty()) {
                    playlistDescription += " - No songs match criteria";
                }
            } else if (p instanceof StandardPlaylist) {
                playlistDescription = "Standard Playlist (Songs: " + songsInPlaylist.size() + ")";
            }

            ImageView coverView = new ImageView();
            try {
                InputStream coverStream = getClass().getResourceAsStream("/images/" + playlistCoverPath);
                if (coverStream != null) {
                    coverView.setImage(new Image(coverStream));
                } else {
                    System.err.println("Cover image not found for playlist " + p.getName() + ": /images/" + playlistCoverPath);
                    coverView.setImage(new Image(getClass().getResourceAsStream("/images/default_album_art.png")));
                }
            } catch (Exception e) {
                System.err.println("Error loading cover for playlist " + p.getName() + ": " + e.getMessage());
                coverView.setImage(new Image(getClass().getResourceAsStream("/images/default_album_art.png")));
            }
            coverView.setFitWidth(120);
            coverView.setFitHeight(120);
            coverView.setPreserveRatio(true);

            PlaylistCard card = new PlaylistCard(p.getName(), playlistDescription, songsInPlaylist, coverView);
            playlistContainer.getChildren().add(card);
        }
        playlistCountText.setText(String.valueOf(playlistContainer.getChildren().size()));
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
                button.setText(tooltipText);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon " + iconPath + ": " + e.getMessage());
            button.setText(tooltipText);
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

        Button saveButton = new Button("Simpan");
        saveButton.getStyleClass().add("save-button");
        saveButton.setOnAction(e -> {
            String newUsername = usernameField.getText().trim();
            String newNickname = nicknameField.getText().trim();
            String oldUserName = user.getUserName();

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

            UserDataUtil.updateUser(oldUserName, user);

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
        settingScene.setFill(javafx.scene.paint.Color.TRANSPARENT);

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
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.setScene(settingScene);
        popupStage.setResizable(false);
        popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
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

        Label typeLabel = new Label("Tipe Playlist:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Standard", "Smart");
        typeComboBox.setValue("Standard");
        typeComboBox.getStyleClass().add("popup-combo-box");

        Label smartCriteriaLabel = new Label("Pilih Genre untuk Smart Playlist:");
        ComboBox<String> smartGenreComboBox = new ComboBox<>();
        List<String> allGenres = UserDataUtil.getDataManager().getSongs().stream()
                                        .map(Song::getGenre)
                                        .filter(genre -> genre != null && !genre.isEmpty())
                                        .distinct()
                                        .sorted()
                                        .collect(Collectors.toList());
        smartGenreComboBox.getItems().addAll(allGenres);
        smartGenreComboBox.setPromptText("Pilih Genre");
        smartGenreComboBox.getStyleClass().add("popup-combo-box");

        VBox smartOptionsBox = new VBox(5, smartCriteriaLabel, smartGenreComboBox);
        smartOptionsBox.setVisible(false);
        smartOptionsBox.setManaged(false);

        Label laguLabel = new Label("Pilih Lagu:");
        List<Song> allAvailableSongs = UserDataUtil.getDataManager().getSongs();
        ListView<Song> laguListView = new ListView<>(FXCollections.observableArrayList(allAvailableSongs));
        laguListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        laguListView.setPrefHeight(120);
        laguListView.setCellFactory(lv -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);
                if (empty || song == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(song.getTitle() + " - " + song.getArtist());
                    try {
                        InputStream coverStream = getClass().getResourceAsStream("/images/" + song.getCover());
                        if (coverStream != null) {
                            imageView.setImage(new Image(coverStream, 32, 32, true, true));
                        } else {
                            System.err.println("Cover image not found for song: " + song.getTitle() + " at /images/" + song.getCover());
                            imageView.setImage(null);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading cover image for " + song.getTitle() + ": " + e.getMessage());
                        imageView.setImage(null);
                    }
                    imageView.setFitWidth(32);
                    imageView.setFitHeight(32);
                    setGraphic(imageView);
                }
            }
        });
        VBox standardOptionsBox = new VBox(5, laguLabel, laguListView);
        standardOptionsBox.setVisible(true);
        standardOptionsBox.setManaged(true);

        // Tombol Simpan dan Batal
        Button simpanBtn = new Button("Simpan"); // <--- DEKLARASI simpanBtn DI SINI
        simpanBtn.getStyleClass().add("primary-button");

        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Smart".equals(newVal)) {
                smartOptionsBox.setVisible(true);
                smartOptionsBox.setManaged(true);
                standardOptionsBox.setVisible(false);
                standardOptionsBox.setManaged(false);
                simpanBtn.setDisable(smartGenreComboBox.getValue() == null || smartGenreComboBox.getValue().isEmpty());
            } else { // Standard
                smartOptionsBox.setVisible(false);
                smartOptionsBox.setManaged(false);
                standardOptionsBox.setVisible(true);
                standardOptionsBox.setManaged(true);
                simpanBtn.setDisable(laguListView.getSelectionModel().isEmpty());
            }
        });

        smartGenreComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            simpanBtn.setDisable(newVal == null || newVal.isEmpty());
        });
        
        laguListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Standard".equals(typeComboBox.getValue())) {
                simpanBtn.setDisable(laguListView.getSelectionModel().isEmpty());
            }
        });
        simpanBtn.setDisable(true);


        simpanBtn.setOnAction(e -> {
            String playlistName = nameField.getText().trim();
            String playlistDesc = descField.getText().trim();
            String playlistType = typeComboBox.getValue();
            
            Playlist newPlaylist = null;

            if (playlistName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Nama playlist tidak boleh kosong!");
                alert.showAndWait();
                return;
            }

            if ("Standard".equals(playlistType)) {
                ObservableList<Song> selectedSongs = laguListView.getSelectionModel().getSelectedItems();
                if (selectedSongs.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Pilih setidaknya satu lagu untuk Standard Playlist!");
                    alert.showAndWait();
                    return;
                }
                List<String> selectedSongIds = selectedSongs.stream()
                                            .map(song -> String.valueOf(song.getId()))
                                            .collect(Collectors.toList());
                
                newPlaylist = new StandardPlaylist(IDGenerator.generateUniqueId(), playlistName, user.getUserName(), selectedSongIds);
            } else if ("Smart".equals(playlistType)) {
                String selectedGenre = smartGenreComboBox.getValue();
                if (selectedGenre == null || selectedGenre.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Pilih genre untuk Smart Playlist!");
                    alert.showAndWait();
                    return;
                }
                SmartPlaylist tempSmartPlaylist = new SmartPlaylist(IDGenerator.generateUniqueId(), playlistName, user.getUserName(), new ArrayList<>());
                tempSmartPlaylist.setGenreCriteria(selectedGenre);
                newPlaylist = tempSmartPlaylist;
            }

            if (newPlaylist != null) {
                UserDataUtil.getDataManager().addPlaylistToUser(user, newPlaylist);
                loadUserPlaylists();
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
                typeLabel, typeComboBox,
                smartOptionsBox,
                standardOptionsBox,
                buttonBox);

        Scene popupScene = new Scene(popupLayout, 400, 450);
        
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
        stopAndDisposeProfileMediaPlayer();

        VBox root = new VBox(24);
        root.getStyleClass().add("popup-player");
        root.setPrefWidth(350);
        root.setMaxWidth(350);
        root.setMinWidth(350);
        root.setPrefHeight(600);

        Label title = new Label(audioFile.getName());
        title.getStyleClass().add("player-title");

        Button playCircle = new Button("▶");
        playCircle.getStyleClass().addAll("circle-btn", "play");
        playCircle.setStyle("-fx-shape: 'M50,10 a40,40 0 1,0 80,0 a40,40 0 1,0 -80,0';");

        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar(0);
        progressBar.getStyleClass().add("progress-bar");

        HBox controls = new HBox(24);
        controls.setAlignment(Pos.CENTER);
        Button pauseBtn = new Button("⏸");
        Button stopBtn = new Button("⏹");
        pauseBtn.getStyleClass().addAll("circle-btn");
        stopBtn.getStyleClass().addAll("circle-btn");
        controls.getChildren().addAll(playCircle, pauseBtn, stopBtn);

        try {
            Media media = new Media(audioFile.toURI().toString());
            currentProfileMediaPlayer = new MediaPlayer(media);
            currentProfileMediaPlayer.setVolume(1.0);
            currentProfileMediaPlayer.currentTimeProperty().addListener((obs, oldVal, newVal) -> {
                if (currentProfileMediaPlayer.getTotalDuration() != null && currentProfileMediaPlayer.getTotalDuration().toMillis() > 0) {
                    double progress = newVal.toMillis() / currentProfileMediaPlayer.getTotalDuration().toMillis();
                    progressBar.setProgress(progress);
                } else {
                    progressBar.setProgress(0);
                }
            });
            currentProfileMediaPlayer.play();
        } catch (Exception ex) {
            System.err.println("Gagal memutar file: " + ex.getMessage());
            currentProfileMediaPlayer = null;
        }

        playCircle.setOnAction(e -> {
            if (currentProfileMediaPlayer != null) {
                currentProfileMediaPlayer.play();
            }
        });

        pauseBtn.setOnAction(e -> {
            if (currentProfileMediaPlayer != null)
                currentProfileMediaPlayer.pause();
        });
        stopBtn.setOnAction(e -> {
            if (currentProfileMediaPlayer != null)
                currentProfileMediaPlayer.stop();
        });

        root.getChildren().addAll(title, progressBar, controls);
        rightBox.getChildren().setAll(root);
    }

    private void stopAndDisposeProfileMediaPlayer() {
        if (currentProfileMediaPlayer != null) {
            currentProfileMediaPlayer.stop();
            currentProfileMediaPlayer.dispose();
            currentProfileMediaPlayer = null;
            System.out.println("DEBUG: MediaPlayer di ProfileView telah dihentikan dan dibuang.");
        }
    }

    public void addPlaylist(String name, String description, ObservableList<Song> songs) {
        displayPlaylistCard(name, description, new ArrayList<>(songs));
    }

    private void displayPlaylistCard(String name, String description, List<Song> songs) {
        String coverPath = "default_album_art.png";
        if (songs != null && !songs.isEmpty() && songs.get(0).getCover() != null) {
            coverPath = songs.get(0).getCover();
        }

        ImageView coverView = new ImageView();
        try {
            InputStream coverStream = getClass().getResourceAsStream("/images/" + coverPath);
            if (coverStream != null) {
                coverView.setImage(new Image(coverStream));
            } else {
                System.err.println("Cover image not found for playlist: /images/" + coverPath);
                coverView.setImage(new Image(getClass().getResourceAsStream("/images/default_album_art.png")));
            }
        } catch (Exception e) {
            System.err.println("Error loading cover image for playlist: " + e.getMessage());
            coverView.setImage(new Image(getClass().getResourceAsStream("/images/default_album_art.png")));
        }
        coverView.setFitWidth(120);
        coverView.setFitHeight(120);
        coverView.setPreserveRatio(true);

        PlaylistCard card = new PlaylistCard(name, description, songs, coverView);
        playlistContainer.getChildren().add(card);
        playlistCountText.setText(String.valueOf(playlistContainer.getChildren().size()));
    }

    private VBox showMusicPlayerPanel(Playlist playlistToPlay) {
        stopAndDisposeProfileMediaPlayer();

        VBox root = new VBox(24);
        root.getStyleClass().add("popup-player");
        root.setPrefWidth(350);
        root.setMaxWidth(350);
        root.setMinWidth(350);
        root.setPrefHeight(600);

        List<Song> songsToPlay;
        if (playlistToPlay instanceof SmartPlaylist) {
            songsToPlay = UserDataUtil.getDataManager().getSongsByIds(null, playlistToPlay);
        } else {
            songsToPlay = UserDataUtil.getDataManager().getSongsByIds(playlistToPlay.getSongIds(), playlistToPlay);
        }
        
        if (songsToPlay.isEmpty()) {
            Label noSongsLabel = new Label("Tidak ada lagu di playlist ini.");
            noSongsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
            root.getChildren().add(noSongsLabel);
            return root;
        }
        
        Song initialSong = songsToPlay.get(0);

        Label title = new Label(initialSong.getTitle());
        title.getStyleClass().add("player-title");
        Label artist = new Label(initialSong.getArtist());
        artist.getStyleClass().add("player-artist");

        VBox info = new VBox(title, artist);
        info.setAlignment(Pos.CENTER);

        ListView<Song> songListView = new ListView<>(FXCollections.observableArrayList(songsToPlay));
        songListView.getStyleClass().add("song-list-view");
        songListView.setPrefHeight(120);
        songListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle() + " - " + item.getArtist());
            }
        });
        songListView.getSelectionModel().select(initialSong);

        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar(0);
        progressBar.getStyleClass().add("progress-bar");

        HBox controls = new HBox(24);
        controls.setAlignment(Pos.CENTER);
        Button prevBtn = new Button("⏮");
        Button playBtn = new Button("▶");
        Button stopBtn = new Button("⏹");
        Button nextBtn = new Button("⏭");
        Button addBtn = new Button("+"); // Tombol tambah lagu ke playlist
        prevBtn.getStyleClass().addAll("circle-btn");
        playBtn.getStyleClass().addAll("circle-btn", "play");
        stopBtn.getStyleClass().addAll("circle-btn");
        nextBtn.getStyleClass().addAll("circle-btn");
        addBtn.getStyleClass().addAll("circle-btn");

        controls.getChildren().addAll(prevBtn, playBtn, stopBtn, nextBtn);

        int[] songIndex = { songsToPlay.indexOf(initialSong) };

        Runnable[] playCurrent = new Runnable[1];
        playCurrent[0] = () -> {
            stopAndDisposeProfileMediaPlayer();
            try {
                String resource = getClass().getResource("/songs/" + songsToPlay.get(songIndex[0]).getFile()).toExternalForm();
                Media media = new Media(resource);
                currentProfileMediaPlayer = new MediaPlayer(media);
                currentProfileMediaPlayer.setVolume(1.0);
                currentProfileMediaPlayer.setOnEndOfMedia(() -> {
                    if (songIndex[0] < songsToPlay.size() - 1) {
                        songIndex[0]++;
                        playCurrent[0].run();
                        songListView.getSelectionModel().select(songIndex[0]);
                    }
                });
                currentProfileMediaPlayer.currentTimeProperty().addListener((obs, oldVal, newVal) -> {
                    if (currentProfileMediaPlayer.getTotalDuration() != null && currentProfileMediaPlayer.getTotalDuration().toMillis() > 0) {
                        double progress = newVal.toMillis() / currentProfileMediaPlayer.getTotalDuration().toMillis();
                        progressBar.setProgress(progress);
                    } else {
                        progressBar.setProgress(0);
                    }
                });
                currentProfileMediaPlayer.play();
                title.setText(songsToPlay.get(songIndex[0]).getTitle());
                artist.setText(songsToPlay.get(songIndex[0]).getArtist());
                songListView.getSelectionModel().select(songIndex[0]);
            } catch (Exception e) {
                System.err.println("Gagal memutar lagu: " + e.getMessage());
                currentProfileMediaPlayer = null;
            }
        };

        playBtn.setOnAction(e -> playCurrent[0].run());
        stopBtn.setOnAction(e -> {
            if (currentProfileMediaPlayer != null)
                currentProfileMediaPlayer.stop();
        });
        nextBtn.setOnAction(e -> {
            if (currentProfileMediaPlayer != null && songIndex[0] < songsToPlay.size() - 1) {
                songIndex[0]++;
                playCurrent[0].run();
            }
        });
        prevBtn.setOnAction(e -> {
            if (currentProfileMediaPlayer != null && songIndex[0] > 0) {
                songIndex[0]--;
                playCurrent[0].run();
            }
        });

        addBtn.setOnAction(e -> {
            if (playlistToPlay instanceof StandardPlaylist) {
                StandardPlaylist standardPlaylist = (StandardPlaylist) playlistToPlay;

                List<Song> allSongs = UserDataUtil.getDataManager().getSongs();
                List<Song> notInPlaylist = allSongs.stream()
                        .filter(s -> !standardPlaylist.getSongIds().contains(String.valueOf(s.getId())))
                        .collect(Collectors.toList());

                ListView<Song> pilihLagu = new ListView<>(FXCollections.observableArrayList(notInPlaylist));
                pilihLagu.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                pilihLagu.setPrefHeight(120);

                Label label = new Label("Pilih Lagu:");
                Button simpan = new Button("Simpan");
                simpan.setOnAction(ev -> {
                    List<Song> selectedToAdd = new java.util.ArrayList<>(pilihLagu.getSelectionModel().getSelectedItems());
                    for (Song s : selectedToAdd) {
                        standardPlaylist.addSong(String.valueOf(s.getId()));
                    }
                    UserDataUtil.getDataManager().updateUserInList(user);

                    songListView.getItems().addAll(selectedToAdd);
                    loadUserPlaylists();
                    ((Stage) simpan.getScene().getWindow()).close();
                });

                VBox popupLayout = new VBox(16, label, pilihLagu, simpan);
                popupLayout.getStyleClass().add("add-song-popup");
                Label infoLabel = new Label("Hanya untuk Standard Playlist.");
                infoLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 10px;");
                popupLayout.getChildren().add(infoLabel);

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

            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Tidak bisa menambah lagu ke Smart Playlist secara manual.");
                alert.showAndWait();
            }
        });
        
        // --- PERBAIKAN: Sembunyikan tombol + jika SmartPlaylist ---
        if (playlistToPlay instanceof SmartPlaylist) {
            addBtn.setVisible(false);
            addBtn.setManaged(false); // Penting agar tidak menyisakan ruang kosong
        } else {
            addBtn.setVisible(true);
            addBtn.setManaged(true);
        }
        // --- AKHIR PERBAIKAN ---

        root.getChildren().addAll(info, songListView, progressBar, controls, addBtn);

        return root;
    }

    class PlaylistCard extends HBox {
        private Playlist playlistData;

        private Label nameLabel;
        private Label descLabel;

        public PlaylistCard(String name, String description, List<Song> songsInDisplay, ImageView coverView) {
            super(20);
            this.setPadding(new Insets(30));
            this.getStyleClass().add("playlist-card");
            this.setAlignment(Pos.CENTER_LEFT);

            VBox info = new VBox();
            nameLabel = new Label(name);
            nameLabel.getStyleClass().add("playlist-name");
            nameLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
            descLabel = new Label(description);
            descLabel.getStyleClass().add("playlist-description");
            descLabel.setStyle("-fx-font-size: 18px;");
            info.getChildren().addAll(nameLabel, descLabel);
            info.setSpacing(10);

            this.getChildren().addAll(coverView, info);

            this.setOnMouseClicked(
                    e -> {
                        Playlist clickedPlaylist = user.getPlaylists().stream()
                                                      .filter(p -> p.getName().equals(name))
                                                      .findFirst()
                                                      .orElse(null);

                        if (clickedPlaylist != null) {
                            stopAndDisposeProfileMediaPlayer();
                            rightBox.getChildren().setAll(showMusicPlayerPanel(clickedPlaylist));
                        } else {
                            System.out.println("Error: Playlist " + name + " not found in user's data.");
                        }
                    });
        }
    }
}