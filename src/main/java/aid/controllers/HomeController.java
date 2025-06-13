package aid.controllers;

import aid.views.HomeView;
import aid.models.Song;
import aid.models.User; // Import User model
import aid.managers.DataManager;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.stage.Stage; // Import Stage
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
    private Stage stage; // Tambahkan field stage
    private User loggedInUser; // Tambahkan field untuk user yang login
    private HomeView view;
    private DataManager dataManager;
    private MediaPlayer mediaPlayer;
    private Song currentPlayingSong;

    private boolean isShuffleOn = false;
    private boolean isRepeatOn = false;
    private List<Song> shuffledSongs;

    // Modifikasi konstruktor untuk menerima Stage dan User
    public HomeController(Stage stage, User user) {
        this.stage = stage;
        this.loggedInUser = user;
        this.view = new HomeView(stage); // HomeView perlu akses ke stage
        this.dataManager = new DataManager(); // Asumsi DataManager tidak perlu stage/user

        // Mengatur callback seeking dari View ke Controller
        this.view.setSeekCallback(progress -> {
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
                // Volume MediaPlayer berkisar dari 0.0 hingga 1.0
                // Slider volume berkisar dari 0 hingga 100
                mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
            }
        });

        // Panggil setupEventHandlers setelah view dibuat dan diinisialisasi
        setupEventHandlers();

        // Load data awal
        loadSongsAndGenres();
        loadInitialPlayerInfo();
    }

    // Metode untuk menampilkan HomeView
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
            currentPlayingSong = songs.get(0);
            view.updateCurrentSongInfo(currentPlayingSong);
            initializeMediaPlayer(currentPlayingSong);
            view.updatePlayPauseButtonIcon(false);
            // Inisialisasi visual tombol kecepatan ke 1x saat aplikasi dimuat
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

        // Menambahkan event handler untuk Home Button (di HomeView, tombol "🏠")
        // ini bukan kembali ke Login, ini untuk "reset" filter di Home
        view.getHomeButton().setOnAction(event -> {
            System.out.println("DEBUG: Home button clicked. Displaying all songs.");
            view.displaySongs(dataManager.getSongs());
            view.getGenreListView().getSelectionModel().clearSelection();
            view.getSearchField().clear(); // Reset search field juga
        });
        
        // --- Perubahan di sini: Menambahkan handler untuk tombol "Profile" (👤) ---
        // Asumsi ada getter untuk tombol profile di HomeView
        view.getProfileButton().setOnAction(event -> {
            System.out.println("DEBUG: Profile button clicked. Navigating to Profile Scene.");
            ProfileController profileController = new ProfileController(stage, loggedInUser);
            profileController.show();
        });
        // --- AKHIR PERUBAHAN ---


        view.getGenreListView().setOnMouseClicked(event -> {
            String selectedGenre = view.getGenreListView().getSelectionModel().getSelectedItem();
            if (selectedGenre != null) {
                filterSongsByGenre(selectedGenre);
            } else {
                view.displaySongs(dataManager.getSongs());
            }
        });

        // Menambahkan event handler untuk semua 8 tombol kecepatan
        view.getSpeed025xButton().setOnAction(e -> setPlaybackSpeed(0.25));
        view.getSpeed05xButton().setOnAction(e -> setPlaybackSpeed(0.5));
        view.getSpeed075xButton().setOnAction(e -> setPlaybackSpeed(0.75));
        view.getSpeed1xButton().setOnAction(e -> setPlaybackSpeed(1.0));
        view.getSpeed125xButton().setOnAction(e -> setPlaybackSpeed(1.25));
        view.getSpeed15xButton().setOnAction(e -> setPlaybackSpeed(1.5));
        view.getSpeed175xButton().setOnAction(e -> setPlaybackSpeed(1.75));
        view.getSpeed2xButton().setOnAction(e -> setPlaybackSpeed(2.0));
    }

    private void initializeMediaPlayer(Song song) {
        // Hanya panggil stop() dan dispose() jika mediaPlayer sudah ada dan sedang memutar
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null; // Penting: Pastikan diset null setelah dispose
        }
        try {
            String resourcePathInClasspath = "/songs/" + song.getFile();
            URL audioUrl = getClass().getResource(resourcePathInClasspath);

            System.out.println("DEBUG: Mencoba memuat dari classpath: " + resourcePathInClasspath);
            System.out.println("DEBUG: URL yang ditemukan: " + audioUrl);

            if (audioUrl == null) {
                System.err.println("ERROR: File musik '" + resourcePathInClasspath + "' tidak ditemukan di classpath. Tidak dapat memutar lagu: " + song.getTitle());
                mediaPlayer = null; // Set ke null jika tidak dapat memuat
                return;
            }

            Media media = new Media(audioUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            // Set volume awal media player sesuai dengan nilai slider saat ini
            mediaPlayer.setVolume(view.getVolumeSlider().getValue() / 100.0);
            // Set kecepatan pemutaran awal media player sesuai dengan tombol 1x
            mediaPlayer.setRate(1.0); // Default ke 1x

            mediaPlayer.setOnReady(() -> {
                System.out.println("DEBUG: Media siap untuk diputar: " + song.getTitle());
                if (mediaPlayer != null && mediaPlayer.getMedia() != null && mediaPlayer.getMedia().getDuration() != Duration.UNKNOWN) { // Pastikan null check
                    view.updateTotalTimeLabel(formatDuration((int)mediaPlayer.getMedia().getDuration().toSeconds()));
                } else {
                    view.updateTotalTimeLabel("0:00"); // Fallback
                }
            });

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (mediaPlayer != null && mediaPlayer.getMedia() != null && mediaPlayer.getMedia().getDuration() != Duration.UNKNOWN) { // Pastikan mediaPlayer dan media tidak null
                    double progress = newValue.toMillis() / mediaPlayer.getMedia().getDuration().toMillis();
                    view.updateProgressBar(progress);
                    view.updateCurrentTimeLabel(formatDuration((int)newValue.toSeconds()));
                }
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("DEBUG: Lagu selesai: " + song.getTitle());
                if (isRepeatOn) {
                    if (mediaPlayer != null) { // Pastikan mediaPlayer tidak null sebelum seek/play
                        mediaPlayer.seek(Duration.ZERO);
                        mediaPlayer.play();
                        System.out.println("DEBUG: Lagu '" + currentPlayingSong.getTitle() + "' diulang.");
                    }
                } else {
                    playNextSong();
                }
            });

            mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
                view.updatePlayPauseButtonIcon(newValue == MediaPlayer.Status.PLAYING);
            });

        } catch (Exception e) {
            System.err.println("Error menginisialisasi media player untuk " + song.getTitle() + ": " + e.getMessage());
            e.printStackTrace();
            mediaPlayer = null; // Penting: Set mediaPlayer ke null jika ada error inisialisasi
        }
    }

    public void playSong(Song song) {
        if (song != null) {
            System.out.println("DEBUG: playSong dipanggil untuk: " + song.getTitle());
            // Logika untuk lagu yang sama dan toggle play/pause
            if (mediaPlayer != null && currentPlayingSong != null && currentPlayingSong.getId() == song.getId() && mediaPlayer.getStatus() != MediaPlayer.Status.STOPPED) {
                System.out.println("DEBUG: Lagu yang sama, toggle Play/Pause.");
                togglePlayPause();
            } else {
                System.out.println("DEBUG: Lagu baru, inisialisasi dan putar.");
                currentPlayingSong = song;
                view.updateCurrentSongInfo(currentPlayingSong);
                initializeMediaPlayer(song); // Inisialisasi MediaPlayer baru
                if (mediaPlayer != null) { // Pastikan mediaPlayer tidak null setelah inisialisasi
                    mediaPlayer.play();
                }
            }
        }
    }

    public void togglePlayPause() {
        if (mediaPlayer != null) { // Tambahkan pengecekan null di sini
            System.out.println("DEBUG: togglePlayPause dipanggil. Status saat ini: " + mediaPlayer.getStatus());
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        }
    }

    private void playNextSong() {
        List<Song> currentSongsList = isShuffleOn ? shuffledSongs : dataManager.getSongs();

        if (currentPlayingSong == null || currentSongsList.isEmpty()) {
            System.out.println("DEBUG: Tidak ada lagu untuk diputar berikutnya.");
            return;
        }

        int currentIndex = -1;
        for (int i = 0; i < currentSongsList.size(); i++) {
            if (currentSongsList.get(i).getId() == currentPlayingSong.getId()) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex != -1) {
            int nextIndex = (currentIndex + 1) % currentSongsList.size();
            Song nextSong = currentSongsList.get(nextIndex);
            System.out.println("DEBUG: Memutar lagu berikutnya: " + nextSong.getTitle());
            playSong(nextSong);
            view.getSongListView().getSelectionModel().select(nextSong);
        } else {
            System.out.println("DEBUG: Lagu saat ini tidak ditemukan di daftar, memutar dari awal atau shuffle.");
            if (!currentSongsList.isEmpty()) {
                playSong(currentSongsList.get(0));
                view.getSongListView().getSelectionModel().select(currentSongsList.get(0));
            }
        }
    }

    private void playPreviousSong() {
        List<Song> currentSongsList = isShuffleOn ? shuffledSongs : dataManager.getSongs();

        if (currentPlayingSong == null || currentSongsList.isEmpty()) {
            System.out.println("DEBUG: Tidak ada lagu untuk diputar sebelumnya.");
            return;
        }

        int currentIndex = -1;
        for (int i = 0; i < currentSongsList.size(); i++) {
            if (currentSongsList.get(i).getId() == currentPlayingSong.getId()) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex != -1) {
            int previousIndex = (currentIndex - 1 + currentSongsList.size()) % currentSongsList.size();
            Song previousSong = currentSongsList.get(previousIndex);
            System.out.println("DEBUG: Memutar lagu sebelumnya: " + previousSong.getTitle());
            playSong(previousSong);
            view.getSongListView().getSelectionModel().select(previousSong);
        } else {
            System.out.println("DEBUG: Lagu saat ini tidak ditemukan di daftar, memutar from awal atau shuffle.");
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
            if (currentPlayingSong != null) {
                view.getSongListView().getSelectionModel().select(currentPlayingSong);
            }
        } else {
            view.displaySongs(dataManager.getSongs());
            if (currentPlayingSong != null) {
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

    // Metode untuk mengatur kecepatan pemutaran
    private void setPlaybackSpeed(double rate) {
        if (mediaPlayer != null) {
            mediaPlayer.setRate(rate);
            view.updateSpeedButtonVisual(rate); // Perbarui visual tombol
            view.showMessage("Kecepatan: " + rate + "x", "info"); // Tampilkan pesan
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