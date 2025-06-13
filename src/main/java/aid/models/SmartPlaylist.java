package aid.models;

import java.util.ArrayList;
import java.util.List;

public class SmartPlaylist extends Playlist {
    
    // PASTIKAN KONSTRUKTOR INI ADA
    public SmartPlaylist(String id, String name, String ownerId, List<String> songIds) {
        super(id, name, ownerId, songIds); // Memanggil konstruktor superclass Playlist
    }

    @Override
    public void addSong(String songId) {
        // Logika SmartPlaylist mungkin tidak mengizinkan penambahan manual
        // atau akan memfilter berdasarkan kriteria.
        // Untuk sekarang, bisa diimplementasikan sama seperti StandardPlaylist
        // atau biarkan kosong jika penambahan otomatis.
        if (!songIds.contains(songId)) {
            songIds.add(songId);
        }
    }

    @Override
    public void removeSong(String songId) {
        songIds.remove(songId);
    }

    @Override
    public String getType() {
        return "Smart";
    }

    @Override
    public String toString() {
        return "Smart" + super.toString();
    }
}
