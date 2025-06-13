package aid.models;

import java.util.ArrayList;
import java.util.List;

public class StandardPlaylist extends Playlist {

    // PASTIKAN KONSTRUKTOR INI ADA
    public StandardPlaylist(String id, String name, String ownerId, List<String> songIds) {
        super(id, name, ownerId, songIds); // Memanggil konstruktor superclass Playlist
    }

    @Override
    public void addSong(String songId) {
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
        return "Standard";
    }

    @Override
    public String toString() {
        return "Standard" + super.toString();
    }
}
