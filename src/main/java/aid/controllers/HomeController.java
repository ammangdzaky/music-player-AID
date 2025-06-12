package aid.controllers;

import aid.views.HomeView;
import aid.models.Song;
import aid.managers.DataManager;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.scene.image.Image;      // Ini masih dibutuhkan untuk album art di HomeView, tapi tidak untuk ikon play/pause
import javafx.scene.image.ImageView;   // Ini masih dibutuhkan untuk album art di HomeView, tapi tidak untuk ikon play/pause
import javafx.scene.control.Label;     // Import Label

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HomeController {
    private HomeView view;
    private DataManager dataManager;
    private MediaPlayer mediaPlayer;
    private Song currentPlayingSong;

    public HomeController(HomeView view) {
        this.view = view;
        this.dataManager = new DataManager();
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

        view.getSearchField().textProperty().addListener((observable, oldValue, newValue) -> {
            filterSongs(newValue);
        });

        view.getGenreListView().setOnMouseClicked(event -> {
            String selectedGenre = view.getGenreListView().getSelectionModel().getSelectedItem();
            if (selectedGenre != null) {
                filterSongsByGenre(selectedGenre);
            } else {
                view.displaySongs(dataManager.getSongs());
            }
        });
    }

    private void initializeMediaPlayer(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        try {
            File mediaFile = new File("resources/songs/" + song.getFile());
            if (!mediaFile.exists()) {
                System.err.println("File musik tidak ditemukan: " + mediaFile.getAbsolutePath());
                return;
            }
            Media media = new Media(mediaFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> {
                System.out.println("Media siap: " + song.getTitle());
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Lagu selesai: " + song.getTitle());
                mediaPlayer.stop();
            });

            // --- PERBAIKAN DI SINI: Gunakan Label dengan Emojis ---
            mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
                Label iconLabel;
                if (newValue == MediaPlayer.Status.PLAYING) {
                    iconLabel = new Label("⏸"); // Emoji Pause
                } else {
                    iconLabel = new Label("▶"); // Emoji Play
                }
                // Atur gaya untuk ikon emoji (sesuaikan dengan style di HomeView createPlayerRoundButton)
                iconLabel.setStyle("-fx-font-size: " + view.getFontSizeLarge() + "; -fx-text-fill: " + view.getBgPrimaryDark() + ";");
                view.getPlayPauseButton().setGraphic(iconLabel);
            });
            // --- AKHIR PERBAIKAN ---

        } catch (Exception e) {
            System.err.println("Error menginisialisasi media player untuk " + song.getTitle() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playSong(Song song) {
        if (song != null) {
            if (mediaPlayer != null && currentPlayingSong != null && currentPlayingSong.getId() == song.getId()) {
                togglePlayPause();
            } else {
                currentPlayingSong = song;
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
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
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
    }

    private void filterSongsByGenre(String genre) {
        List<Song> filteredSongs = dataManager.getSongs().stream()
                .filter(song -> song.getGenre() != null && song.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
        view.displaySongs(filteredSongs);
    }

    public List<Song> getRecommendedSongs() {
        return dataManager.getSongs().stream()
                   .sorted(Comparator.comparing(Song::getTitle))
                   .collect(Collectors.toList());
    }
}
