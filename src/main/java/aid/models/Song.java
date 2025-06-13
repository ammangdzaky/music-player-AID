package aid.models;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int durationSeconds;
    private String file;
    private String cover;

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

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getGenre() { return genre; }
    public int getDurationSeconds() { return durationSeconds; }
    public String getFile() { return file; }
    public String getCover() { return cover; }

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
