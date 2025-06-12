package aid.controllers;

import aid.views.HomeView;
import aid.models.Song;
import aid.managers.DataManager;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image; 	 	
import javafx.scene.image.ImageView; 	
import javafx.scene.control.Label; 	 	
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
    private HomeView view;
    private DataManager dataManager;
    private MediaPlayer mediaPlayer;
    private Song currentPlayingSong;

    private boolean isShuffleOn = false;
    private boolean isRepeatOn = false;
    private List<Song> shuffledSongs;

    public HomeController(HomeView view) {
        this.view = view;
        this.dataManager = new DataManager();
        
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

            System.out.println("DEBUG: Mencoba memuat dari classpath: " + resourcePathInClasspath);
            System.out.println("DEBUG: URL yang ditemukan: " + audioUrl);

            if (audioUrl == null) {
                System.err.println("ERROR: File musik '" + resourcePathInClasspath + "' tidak ditemukan di classpath. Tidak dapat memutar lagu: " + song.getTitle());
                return;
            }

            Media media = new Media(audioUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            // Set volume awal media player sesuai dengan nilai slider saat ini
            mediaPlayer.setVolume(view.getVolumeSlider().getValue() / 100.0);

            mediaPlayer.setOnReady(() -> {
                System.out.println("DEBUG: Media siap untuk diputar: " + song.getTitle());
                view.updateTotalTimeLabel(formatDuration((int)mediaPlayer.getMedia().getDuration().toSeconds()));
            });

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (mediaPlayer.getMedia().getDuration() != Duration.UNKNOWN) {
                    double progress = newValue.toMillis() / mediaPlayer.getMedia().getDuration().toMillis();
                    view.updateProgressBar(progress);
                    view.updateCurrentTimeLabel(formatDuration((int)newValue.toSeconds()));
                }
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("DEBUG: Lagu selesai: " + song.getTitle());
                if (isRepeatOn) {
                    playSong(currentPlayingSong);
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
        }
    }

    public void playSong(Song song) {
        if (song != null) {
            System.out.println("DEBUG: playSong dipanggil untuk: " + song.getTitle());
            if (mediaPlayer != null && currentPlayingSong != null && currentPlayingSong.getId() == song.getId()) {
                System.out.println("DEBUG: Lagu yang sama, toggle Play/Pause.");
                togglePlayPause();
            } else {
                System.out.println("DEBUG: Lagu baru, inisialisasi dan putar.");
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
            System.out.println("DEBUG: Lagu saat ini tidak ditemukan di daftar, memutar dari awal atau shuffle.");
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