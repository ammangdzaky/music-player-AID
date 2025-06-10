package aid.models;

public class Song {
    private String id;
    private String title;
    private String artist;
    private String album;
    private String genre; // Atribut genre baru
    private int durationSeconds; // Durasi dalam detik
    private String filePath; // Path ke file audio

    // Constructor
    public Song(String id, String title, String artist, String album, String genre, int durationSeconds, String filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre; // Inisialisasi genre
        this.durationSeconds = durationSeconds;
        this.filePath = filePath;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getGenre() { return genre; } // Getter untuk genre
    public int getDurationSeconds() { return durationSeconds; }
    public String getFilePath() { return filePath; }

    // Setters (jika properti bisa diubah setelah pembuatan, misal update genre)
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Song{" +
               "id='" + id + '\'' +
               ", title='" + title + '\'' +
               ", artist='" + artist + '\'' +
               ", album='" + album + '\'' +
               ", genre='" + genre + '\'' +
               ", durationSeconds=" + durationSeconds +
               ", filePath='" + filePath + '\'' +
               '}';
    }
}