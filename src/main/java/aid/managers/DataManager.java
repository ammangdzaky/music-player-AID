package aid.managers;

import aid.models.Song;
import aid.models.User;
import aid.models.Playlist;
import aid.models.StandardPlaylist;
import aid.models.SmartPlaylist;
import aid.utils.IDGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {
    private static final String USERS_FILE = "data/users.json";
    private static final String PLAYLISTS_FILE = "data/playlists.json";
    private static final String SONGS_RESOURCE_PATH = "/songs.json"; // PATH SONGS.JSON DARI ROOT RESOURCES

    private Gson gson;

    private List<User> users;
    private List<Song> songs;
    private List<Playlist> playlists;

    public DataManager() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Playlist.class, new PlaylistTypeAdapter())
                .create();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();

        ensureDataDirectoryAndFilesExist();
        loadAllData();
        // createDummyData(); // Sekarang data akan dimuat dari JSON, jadi ini bisa dikomentari
    }

    private void ensureDataDirectoryAndFilesExist() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            if (dataDir.mkdir()) {
                System.out.println("Created data directory: " + dataDir.getAbsolutePath());
            } else {
                System.err.println("Failed to create data directory: " + dataDir.getAbsolutePath());
            }
        }
        createEmptyJsonFileIfNotExist(USERS_FILE);
        createEmptyJsonFileIfNotExist(PLAYLISTS_FILE);
    }

    private void createEmptyJsonFileIfNotExist(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("[]");
                System.out.println("Created empty JSON file: " + filePath);
            } catch (IOException e) {
                System.err.println("Error creating empty JSON file " + filePath + ": " + e.getMessage());
            }
        }
    }

    private void loadAllData() {
        loadUsers();
        loadSongs();
        loadPlaylists();
    }

    // --- User Data Management ---
    public List<User> getUsers() { return new ArrayList<>(users); }
    public void loadUsers() {
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            users = gson.fromJson(reader, userListType);
            if (users == null) users = new ArrayList<>();
            System.out.println("Users loaded: " + users.size());
        } catch (IOException e) {
            System.err.println("Could not load users.json: " + e.getMessage());
            users = new ArrayList<>();
        }
    }

    public void saveUsers() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
            System.out.println("Users saved: " + users.size());
        } catch (IOException e) {
            System.err.println("Could not save users.json: " + e.getMessage());
        }
    }

    public void addUser(User user) { /* ... */ }
    public User getUserByUsername(String username) { /* ... */ return null; }
    public User getUserById(String id) { /* ... */ return null; }


    // --- Song Data Management ---
    public List<Song> getSongs() { return new ArrayList<>(songs); }
    public void loadSongs() {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(SONGS_RESOURCE_PATH))) {
            Type songListType = new TypeToken<ArrayList<Song>>() {}.getType();
            songs = gson.fromJson(reader, songListType);
            if (songs == null) songs = new ArrayList<>();
            System.out.println("Songs loaded: " + songs.size());
        } catch (NullPointerException e) {
            System.err.println("Resource not found: " + SONGS_RESOURCE_PATH + ". Make sure it's in src/main/resources/songs.json or src/main/resources/.");
            songs = new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error reading songs.json from resources: " + e.getMessage());
            songs = new ArrayList<>();
        }
    }

    public Song getSongById(int id) { // ID sekarang int
        return songs.stream()
                .filter(song -> song.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // --- Playlist Data Management ---
    public List<Playlist> getPlaylists() { return new ArrayList<>(playlists); }
    public void loadPlaylists() { /* ... */ }
    public void savePlaylists() { /* ... */ }
    public void addPlaylist(Playlist playlist) { /* ... */ }
    public Playlist getPlaylistById(String id) { /* ... */ return null; }
}
