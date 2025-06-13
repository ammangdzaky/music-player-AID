package aid.controllers;

import aid.views.HomeView;
import aid.models.Song;
import aid.models.User;
import aid.managers.DataManager;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URI;
import java.net.URL;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.ArrayList;

public class HomeController {
    private Stage stage;
    private User loggedInUser;
    private HomeView view;
    private DataManager dataManager;
    private MediaPlayer mediaPlayer;
    private Song currentPlayingSong; // <--- PERBAIKAN: Pindahkan deklarasi ke sini

    private boolean isShuffleOn = false;
    private boolean isRepeatOn = false;
    private List<Song> shuffledSongs;

    public HomeController(Stage stage, User user) {
        this.stage = stage;
        this.loggedInUser = user;
        this.view = new HomeView(stage);
        this.dataManager = new DataManager();

        // Mengatur callback seeking dari View ke Controller
        this.view.setSeekCallback(progress -> {
            // currentPlayingSong sudah dideklarasikan di scope kelas
            if (mediaPlayer != null && currentPlayingSong != null) {
                Duration totalDuration = mediaPlayer.getMedia().getDuration();
                if (totalDuration != Duration.UNKNOWN) {
                    mediaPlayer.seek(totalDuration.multiply(progress));
                }
            }
        });

        // Menambahkan listener untuk volumeSlider
        this.view.getVolumeSlider().valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
            }
        });

        setupEventHandlers(); // Dipanggil setelah view dibuat

        loadSongsAndGenres();
        loadInitialPlayerInfo();
    }

    public void show() {
        stage.setScene(view.getScene());
        stage.setTitle("AID Music Player - Home");
        stage.setFullScreen(true);
        stage.show();
    }

    public void loadSongsAndGenres() {
        List<Song> allSongs = dataManager.getSongs();
        view.displaySongs(allSongs);

        Set<String> uniqueGenres = allSongs.stream()
                .map(Song::getGenre)
                .filter(genre -> genre != null && !genre.isEmpty())
                .collect(Collectors.toSet());
        view.displayGenres(uniqueGenres.stream().sorted().collect(Collectors.toList()));
    }

    public void loadInitialPlayerInfo() {
        List<Song> songs = dataManager.getSongs();
        if (!songs.isEmpty()) {
            currentPlayingSong = songs.get(0); // Akses di sini seharusnya sudah tidak error
            view.updateCurrentSongInfo(currentPlayingSong);
            initializeMediaPlayer(currentPlayingSong);
            view.updatePlayPauseButtonIcon(false);
            view.updateSpeedButtonVisual(1.0);
        } else {
            view.updateCurrentSongInfo(null);
        }
    }

    public void setupEventHandlers() {
        view.getSongListView().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Song selectedSong = view.getSongListView().getSelectionModel().getSelectedItem();
                if (selectedSong != null) {
                    playSong(selectedSong);
                }
            }
        });

        view.getPlayPauseButton().setOnAction(event -> togglePlayPause());
        view.getPrevButton().setOnAction(event -> playPreviousSong());
        view.getNextButton().setOnAction(event -> playNextSong());
        view.getShuffleButton().setOnAction(event -> toggleShuffle());
        view.getRepeatButton().setOnAction(event -> toggleRepeat());

        view.getSearchField().textProperty().addListener((observable, oldValue, newValue) -> {
            filterSongs(newValue);
        });

        view.getHomeButton().setOnAction(event -> {
            System.out.println("DEBUG: Home button clicked. Displaying all songs.");
            view.displaySongs(dataManager.getSongs());
            view.getGenreListView().getSelectionModel().clearSelection();
            view.getSearchField().clear();
        });
        
        view.getProfileButton().setOnAction(event -> {
            System.out.println("DEBUG: Profile button clicked. Navigating to Profile Scene.");
            stopAndDisposeMediaPlayer();
            ProfileController profileController = new ProfileController(stage, loggedInUser);
            profileController.show();
        });

        view.getGenreListView().setOnMouseClicked(event -> {
            String selectedGenre = view.getGenreListView().getSelectionModel().getSelectedItem();
            if (selectedGenre != null) {
                filterSongsByGenre(selectedGenre);
            } else {
                view.displaySongs(dataManager.getSongs());
            }
        });

        view.getSpeed025xButton().setOnAction(e -> setPlaybackSpeed(0.25));
        view.getSpeed05xButton().setOnAction(e -> setPlaybackSpeed(0.5));
        view.getSpeed075xButton().setOnAction(e -> setPlaybackSpeed(0.75));
        view.getSpeed1xButton().setOnAction(e -> setPlaybackSpeed(1.0));
        view.getSpeed125xButton().setOnAction(e -> setPlaybackSpeed(1.25));
        view.getSpeed15xButton().setOnAction(e -> setPlaybackSpeed(1.5));
        view.getSpeed175xButton().setOnAction(e -> setPlaybackSpeed(1.75));
        view.getSpeed2xButton().setOnAction(e -> setPlaybackSpeed(2.0));
    }

    private void stopAndDisposeMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
            System.out.println("DEBUG: MediaPlayer in HomeController has been stopped and disposed.");
        }
    }

    private void initializeMediaPlayer(Song song) {
        stopAndDisposeMediaPlayer(); // Ensure previous MediaPlayer is disposed

        try {
            String resourcePathInClasspath = "/songs/" + song.getFile();
            URL audioUrl = getClass().getResource(resourcePathInClasspath);

            System.out.println("DEBUG: Attempting to load from classpath: " + resourcePathInClasspath);
            System.out.println("DEBUG: Found URL: " + audioUrl);

            if (audioUrl == null) {
                System.err.println("ERROR: Music file '" + resourcePathInClasspath + "' not found in classpath. Cannot play song: " + song.getTitle());
                mediaPlayer = null;
                return;
            }

            Media media = new Media(audioUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setVolume(view.getVolumeSlider().getValue() / 100.0);
            mediaPlayer.setRate(1.0);

            mediaPlayer.setOnReady(() -> {
                System.out.println("DEBUG: Media is ready to play: " + song.getTitle());
                if (mediaPlayer != null && mediaPlayer.getMedia() != null && mediaPlayer.getMedia().getDuration() != Duration.UNKNOWN) {
                    view.updateTotalTimeLabel(formatDuration((int)mediaPlayer.getMedia().getDuration().toSeconds()));
                } else {
                    view.updateTotalTimeLabel("0:00");
                }
            });

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (mediaPlayer != null && mediaPlayer.getMedia() != null && mediaPlayer.getMedia().getDuration() != Duration.UNKNOWN) {
                    double progress = newValue.toMillis() / mediaPlayer.getMedia().getDuration().toMillis();
                    view.updateProgressBar(progress);
                    view.updateCurrentTimeLabel(formatDuration((int)newValue.toSeconds()));
                }
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("DEBUG: Song finished: " + song.getTitle());
                if (isRepeatOn) {
                    if (mediaPlayer != null) {
                        mediaPlayer.seek(Duration.ZERO);
                        mediaPlayer.play();
                        System.out.println("DEBUG: Song '" + currentPlayingSong.getTitle() + "' is repeating."); // Akses di sini
                    }
                } else {
                    playNextSong();
                }
            });

            mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
                view.updatePlayPauseButtonIcon(newValue == MediaPlayer.Status.PLAYING);
            });

        } catch (Exception e) {
            System.err.println("Error initializing media player for " + song.getTitle() + ": " + e.getMessage());
            e.printStackTrace();
            mediaPlayer = null;
        }
    }

    public void playSong(Song song) {
        if (song != null) {
            System.out.println("DEBUG: playSong called for: " + song.getTitle());
            if (mediaPlayer != null && currentPlayingSong != null && currentPlayingSong.getId() == song.getId() && mediaPlayer.getStatus() != MediaPlayer.Status.STOPPED) {
                System.out.println("DEBUG: Same song, toggling Play/Pause.");
                togglePlayPause();
            } else {
                System.out.println("DEBUG: New song, initializing and playing.");
                currentPlayingSong = song; // Akses di sini
                view.updateCurrentSongInfo(currentPlayingSong);
                initializeMediaPlayer(song);
                if (mediaPlayer != null) {
                    mediaPlayer.play();
                }
            }
        }
    }

    public void togglePlayPause() {
        if (mediaPlayer != null) {
            System.out.println("DEBUG: togglePlayPause called. Current status: " + mediaPlayer.getStatus());
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        }
    }

    private void playNextSong() {
        List<Song> currentSongsList = isShuffleOn ? shuffledSongs : dataManager.getSongs();

        if (currentPlayingSong == null || currentSongsList.isEmpty()) { // Akses di sini
            System.out.println("DEBUG: No song to play next.");
            return;
        }

        int currentIndex = -1;
        for (int i = 0; i < currentSongsList.size(); i++) {
            if (currentSongsList.get(i).getId() == currentPlayingSong.getId()) { // Akses di sini
                currentIndex = i;
                break;
            }
        }

        if (currentIndex != -1) {
            int nextIndex = (currentIndex + 1) % currentSongsList.size();
            Song nextSong = currentSongsList.get(nextIndex);
            System.out.println("DEBUG: Playing next song: " + nextSong.getTitle());
            playSong(nextSong);
            view.getSongListView().getSelectionModel().select(nextSong);
        } else {
            System.out.println("DEBUG: Current song not found in list, playing from beginning or shuffle.");
            if (!currentSongsList.isEmpty()) {
                playSong(currentSongsList.get(0));
                view.getSongListView().getSelectionModel().select(currentSongsList.get(0));
            }
        }
    }

    private void playPreviousSong() {
        List<Song> currentSongsList = isShuffleOn ? shuffledSongs : dataManager.getSongs();

        if (currentPlayingSong == null || currentSongsList.isEmpty()) { // Akses di sini
            System.out.println("DEBUG: No song to play previously.");
            return;
        }

        int currentIndex = -1;
        for (int i = 0; i < currentSongsList.size(); i++) {
            if (currentSongsList.get(i).getId() == currentPlayingSong.getId()) { // Akses di sini
                currentIndex = i;
                break;
            }
        }

        if (currentIndex != -1) {
            int previousIndex = (currentIndex - 1 + currentSongsList.size()) % currentSongsList.size();
            Song previousSong = currentSongsList.get(previousIndex);
            System.out.println("DEBUG: Playing previous song: " + previousSong.getTitle());
            playSong(previousSong);
            view.getSongListView().getSelectionModel().select(previousSong);
        } else {
            System.out.println("DEBUG: Current song not found in list, playing from beginning or shuffle.");
            if (!currentSongsList.isEmpty()) {
                playSong(currentSongsList.get(0));
                view.getSongListView().getSelectionModel().select(currentSongsList.get(0));
            }
        }
    }

    private void toggleShuffle() {
        isShuffleOn = !isShuffleOn;
        System.out.println("DEBUG: Shuffle is now: " + (isShuffleOn ? "ON" : "OFF"));

        if (isShuffleOn) {
            shuffledSongs = new ArrayList<>(dataManager.getSongs());
            Collections.shuffle(shuffledSongs);
            view.displaySongs(shuffledSongs);
            if (currentPlayingSong != null) { // Akses di sini
                view.getSongListView().getSelectionModel().select(currentPlayingSong);
            }
        } else {
            view.displaySongs(dataManager.getSongs());
            if (currentPlayingSong != null) { // Akses di sini
                view.getSongListView().getSelectionModel().select(currentPlayingSong);
            }
        }
    }

    private void toggleRepeat() {
        isRepeatOn = !isRepeatOn;
        System.out.println("DEBUG: Repeat is now: " + (isRepeatOn ? "ON" : "OFF"));
        view.updateRepeatButtonVisual(isRepeatOn);
        view.showMessage("Mode Ulangi: " + (isRepeatOn ? "AKTIF" : "NONAKTIF"), "info");
    }

    private void setPlaybackSpeed(double rate) {
        if (mediaPlayer != null) {
            mediaPlayer.setRate(rate);
            view.updateSpeedButtonVisual(rate);
            view.showMessage("Kecepatan: " + rate + "x", "info");
        }
    }

    private void filterSongs(String searchText) {
        List<Song> filteredSongs = dataManager.getSongs().stream()
                .filter(song -> song.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                                 song.getArtist().toLowerCase().contains(searchText.toLowerCase()) ||
                                 song.getAlbum().toLowerCase().contains(searchText.toLowerCase()) ||
                                 (song.getGenre() != null && song.getGenre().toLowerCase().contains(searchText.toLowerCase())))
                .collect(Collectors.toList());
        view.displaySongs(filteredSongs);
        if (!filteredSongs.isEmpty()) {
            view.getSongListView().getSelectionModel().select(filteredSongs.get(0));
        }
    }

    private void filterSongsByGenre(String genre) {
        List<Song> filteredSongs = dataManager.getSongs().stream()
                .filter(song -> song.getGenre() != null && song.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
        view.displaySongs(filteredSongs);
        if (!filteredSongs.isEmpty()) {
            view.getSongListView().getSelectionModel().select(filteredSongs.get(0));
        }
    }

    public List<Song> getRecommendedSongs() {
        return dataManager.getSongs().stream()
                .sorted(Comparator.comparing(Song::getTitle))
                .collect(Collectors.toList());
    }

    private String formatDuration(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}