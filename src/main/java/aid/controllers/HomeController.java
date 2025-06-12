package aid.controllers;

import aid.views.HomeView;
import aid.models.Song;
import aid.managers.DataManager;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image;      
import javafx.scene.image.ImageView;   
import javafx.scene.control.Label;     

import java.io.File;
import java.net.URI; 
import java.net.URL; 

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

        // --- Tambahkan Event Handler untuk Tombol Previous dan Next ---
        view.getPrevButton().setOnAction(event -> playPreviousSong());
        view.getNextButton().setOnAction(event -> playNextSong());
        // --- AKHIR TAMBAHAN ---


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
            String resourcePathInClasspath = "/songs/" + song.getFile(); 
            URL audioUrl = getClass().getResource(resourcePathInClasspath);

            if (audioUrl == null) {
                System.err.println("Gagal menemukan file musik di classpath: " + resourcePathInClasspath);
                File mediaFileFallback = new File("resources/songs/" + song.getFile());
                if (mediaFileFallback.exists()) {
                    System.err.println("Namun, file ditemukan melalui jalur sistem file (FALLBACK): " + mediaFileFallback.getAbsolutePath());
                    Media media = new Media(mediaFileFallback.toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
                } else {
                    System.err.println("File musik juga tidak ditemukan di jalur sistem file: " + mediaFileFallback.getAbsolutePath());
                    System.err.println("Error: File musik tidak ditemukan untuk " + song.getTitle());
                    return; 
                }
            } else {
                Media media = new Media(audioUrl.toExternalForm());
                mediaPlayer = new MediaPlayer(media);
            }

            mediaPlayer.setOnReady(() -> {
                System.out.println("Media siap: " + song.getTitle());
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Lagu selesai: " + song.getTitle());
                // Otomatis putar lagu berikutnya saat lagu selesai
                playNextSong(); // Panggil playNextSong di sini
            });

            mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
                Label iconLabel;
                if (newValue == MediaPlayer.Status.PLAYING) {
                    iconLabel = new Label("⏸");
                } else {
                    iconLabel = new Label("▶");
                }
                iconLabel.setStyle("-fx-font-size: " + view.getFontSizeLarge() + "; -fx-text-fill: " + view.getBgPrimaryDark() + ";");
                view.getPlayPauseButton().setGraphic(iconLabel);
            });

        } catch (Exception e) {
            System.err.println("Error menginisialisasi media player untuk " + song.getTitle() + ": " + e.getMessage());
            e.printStackTrace();
            System.err.println("Error: Gagal memutar lagu: " + song.getTitle() + " - " + e.getMessage());
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

    // --- LOGIKA PLAY PREVIOUS DAN NEXT SONG (BARU) ---
    private void playNextSong() {
        if (currentPlayingSong == null || view.getSongListView().getItems().isEmpty()) {
            return; // Tidak ada lagu yang diputar atau daftar kosong
        }

        List<Song> currentSongsInView = view.getSongListView().getItems();
        int currentIndex = -1;
        for (int i = 0; i < currentSongsInView.size(); i++) {
            if (currentSongsInView.get(i).getId() == currentPlayingSong.getId()) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex != -1) {
            int nextIndex = (currentIndex + 1) % currentSongsInView.size(); // Kembali ke awal jika sudah di akhir
            Song nextSong = currentSongsInView.get(nextIndex);
            playSong(nextSong);
            view.getSongListView().getSelectionModel().select(nextSong); // Pilih lagu di ListView
        }
    }

    private void playPreviousSong() {
        if (currentPlayingSong == null || view.getSongListView().getItems().isEmpty()) {
            return; // Tidak ada lagu yang diputar atau daftar kosong
        }

        List<Song> currentSongsInView = view.getSongListView().getItems();
        int currentIndex = -1;
        for (int i = 0; i < currentSongsInView.size(); i++) {
            if (currentSongsInView.get(i).getId() == currentPlayingSong.getId()) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex != -1) {
            int previousIndex = (currentIndex - 1 + currentSongsInView.size()) % currentSongsInView.size(); // Kembali ke akhir jika sudah di awal
            Song previousSong = currentSongsInView.get(previousIndex);
            playSong(previousSong);
            view.getSongListView().getSelectionModel().select(previousSong); // Pilih lagu di ListView
        }
    }
    // --- AKHIR LOGIKA PLAY PREVIOUS DAN NEXT SONG ---


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
