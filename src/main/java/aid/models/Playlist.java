package aid.models;

import java.util.List;
import java.util.ArrayList; // Pastikan ini diimpor juga

public abstract class Playlist {
    protected String id;
    protected String name;
    protected String ownerId;
    protected List<String> songIds;

    public Playlist(String id, String name, String ownerId, List<String> songIds) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.songIds = songIds != null ? new ArrayList<>(songIds) : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public List<String> getSongIds() {
        return songIds;
    }

    // Ini adalah metode abstrak yang harus diimplementasikan oleh subkelas
    public abstract void addSong(String songId);
    public abstract void removeSong(String songId);
    public abstract String getType(); // Metode ini dipanggil oleh PlaylistTypeAdapter

    public void setName(String name) {
        this.name = name;
    }
    
    // Metode untuk menambahkan songId ke daftar songIds
    public void addSongId(String songId) {
        if (!songIds.contains(songId)) {
            songIds.add(songId);
        }
    }

    // Metode untuk menghapus songId dari daftar songIds
    public void removeSongId(String songId) {
        songIds.remove(songId);
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", songIds=" + songIds +
                '}';
    }
}