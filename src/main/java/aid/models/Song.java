package aid.models;

public class Song {
    private int id; // ID dalam bentuk int
    private String title;
    private String artist;
    private String album;   // Tambahkan kembali jika album ingin ditampilkan di UI atau jika ada di JSON
    private String genre;   // Atribut genre
    private int durationSeconds; // Durasi dalam detik (jika ingin disimpan)
    private String file;    // Path ke file audio (seperti di JSON Anda)
    private String cover;   // Path ke cover art (seperti di JSON Anda)

    // Konstruktor disesuaikan dengan atribut yang ada di JSON dan yang Anda inginkan
    public Song(int id, String title, String artist, String album, String genre, int durationSeconds, String file, String cover) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.durationSeconds = durationSeconds;
        this.file = file;
        this.cover = cover;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getGenre() { return genre; }
    public int getDurationSeconds() { return durationSeconds; }
    public String getFile() { return file; }
    public String getCover() { return cover; }

    // Setters (jika properti ini bisa diubah setelah dibuat)
    public void setAlbum(String album) { this.album = album; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    public void setFile(String file) { this.file = file; }
    public void setCover(String cover) { this.cover = cover; }


    @Override
    public String toString() {
        return "Song{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", artist='" + artist + '\'' +
               ", album='" + album + '\'' +
               ", genre='" + genre + '\'' +
               ", durationSeconds=" + durationSeconds +
               ", file='" + file + '\'' +
               ", cover='" + cover + '\'' +
               '}';
    }
}
