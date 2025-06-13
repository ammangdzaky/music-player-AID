package aid.utils;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import aid.models.Song;

public class songUtils {
    public static List<Song> loadSongsFromJson(Class<?> contextClass) {
        List<Song> songs = new ArrayList<>();
        try (InputStream is = contextClass.getResourceAsStream("/data/songs.json")) {
            if (is != null) {
                String json = new String(is.readAllBytes());
                Gson gson = new Gson();
                songs = gson.fromJson(json, new TypeToken<java.util.List<aid.models.Song>>() {
                }.getType());
            }
        } catch (Exception e) {
            System.err.println("Error loading songs: " + e.getMessage());
        }
        return songs;
    }
}