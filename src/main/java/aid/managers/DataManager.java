package aid.managers;

import aid.models.Song;
import aid.models.User;
import aid.models.Playlist;
import aid.models.StandardPlaylist;
import aid.models.SmartPlaylist; // Import SmartPlaylist
import aid.utils.IDGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

public class DataManager {
    private static final String USERS_FILE = "data/users.json";
    private static final String SONGS_RESOURCE_PATH = "/data/songs.json"; 

    private Gson gson;

    private List<User> users;
    private List<Song> songs;

    public DataManager() {
        System.out.println("DEBUG: DataManager constructor invoked.");
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Playlist.class, new PlaylistTypeAdapter())
                .create();
        System.out.println("DEBUG: Gson initialized in DataManager.");

        users = new ArrayList<>();
        songs = new ArrayList<>();

        ensureDataDirectoryAndFilesExist();
        System.out.println("DEBUG: Data directory and files ensured.");
        loadAllData();
        System.out.println("DEBUG: All data loaded in DataManager.");
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
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void loadUsers() {
        System.out.println("DEBUG: Loading users from " + USERS_FILE);
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            users = gson.fromJson(reader, userListType);
            if (users == null) users = new ArrayList<>();
            System.out.println("Users loaded: " + users.size());
            users.forEach(user -> System.out.println("User: " + user.getUserName() + ", Playlists: " + user.getPlaylists().size()));
        } catch (IOException e) {
            System.err.println("Could not load users.json: " + e.getMessage());
            users = new ArrayList<>();
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing users.json (JsonSyntaxException): " + e.getMessage());
            e.printStackTrace();
            users = new ArrayList<>();
        }
    }

    public void saveUsers() {
        System.out.println("DEBUG: Saving users to " + USERS_FILE);
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
            System.out.println("Users saved: " + users.size());
        } catch (IOException e) {
            System.err.println("Could not save users.json: " + e.getMessage());
        }
    }

    public void addUser(User user) {
        if (!isUsernameTaken(user.getUserName())) {
            this.users.add(user);
            saveUsers();
            System.out.println("New user added: " + user.getUserName());
        } else {
            System.err.println("Username " + user.getUserName() + " is already taken.");
        }
    }
    
    public void updateUserInList(User updatedUser) {
        boolean found = false;
        for (int i = 0; i < this.users.size(); i++) {
            if (this.users.get(i).getUserName().equals(updatedUser.getUserName())) {
                this.users.set(i, updatedUser);
                found = true;
                break;
            }
        }
        if (found) {
            saveUsers();
            System.out.println("User " + updatedUser.getUserName() + " updated and saved.");
        } else {
            System.err.println("User " + updatedUser.getUserName() + " not found for update.");
        }
    }

    public User getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUserName().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }
    
    public User getUserById(String id) {
        return getUserByUsername(id);
    }

    public boolean isUsernameTaken(String username) {
        return users.stream().anyMatch(user -> user.getUserName().equalsIgnoreCase(username));
    }

    public List<Song> getSongs() { return new ArrayList<>(songs); }
    public void loadSongs() {
        System.out.println("DEBUG: Loading songs from resource: " + SONGS_RESOURCE_PATH);
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(SONGS_RESOURCE_PATH))) {
            if (reader == null) {
                System.err.println("ERROR: InputStream for " + SONGS_RESOURCE_PATH + " is null. Resource not found.");
                songs = new ArrayList<>();
                return;
            }
            Type songListType = new TypeToken<ArrayList<Song>>() {}.getType();
            songs = gson.fromJson(reader, songListType);
            if (songs == null) songs = new ArrayList<>();
            System.out.println("Songs loaded: " + songs.size());
        } catch (NullPointerException e) {
            System.err.println("Resource not found (NullPointerException): " + SONGS_RESOURCE_PATH + ". Make sure it's in src/main/resources/data/songs.json.");
            songs = new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error reading songs.json from resources (IOException): " + e.getMessage());
            e.printStackTrace();
            songs = new ArrayList<>();
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing songs.json (JsonSyntaxException): " + e.getMessage());
            e.printStackTrace();
            songs = new ArrayList<>();
        }
    }

    public Song getSongById(String id) {
        try {
            int songIntId = Integer.parseInt(id);
            return songs.stream()
                    .filter(song -> song.getId() == songIntId)
                    .findFirst()
                    .orElse(null);
        } catch (NumberFormatException e) {
            System.err.println("Invalid song ID format: " + id);
            return null;
        }
    }
    
    // --- PERUBAHAN: getSongsByIds sekarang juga bisa memproses SmartPlaylist ---
    public List<Song> getSongsByIds(List<String> songIds, Playlist playlist) {
        if (playlist instanceof SmartPlaylist) {
            SmartPlaylist smartPlaylist = (SmartPlaylist) playlist;
            // Gunakan metode generateSongIds dari SmartPlaylist
            List<String> generatedIds = smartPlaylist.generateSongIds(this.songs); // Ambil semua lagu dari DataManager
            return generatedIds.stream()
                               .map(this::getSongById)
                               .filter(java.util.Objects::nonNull)
                               .collect(Collectors.toList());
        } else { // StandardPlaylist atau tipe lain
            if (songIds == null || songIds.isEmpty()) {
                return new ArrayList<>();
            }
            return songIds.stream()
                          .map(this::getSongById)
                          .filter(java.util.Objects::nonNull)
                          .collect(Collectors.toList());
        }
    }

    // Overload metode asli untuk kompatibilitas jika masih ada panggilan yang hanya meneruskan List<String>
    public List<Song> getSongsByIds(List<String> songIds) {
        // Ini akan berfungsi untuk StandardPlaylist yang songIds-nya sudah permanen
        // Namun, jika dipanggil untuk SmartPlaylist, ia tidak akan tahu kriterianya.
        // Sebaiknya, panggil getSongsByIds(List<String> songIds, Playlist playlist)
        // jika Anda tahu objek Playlist-nya.
        // Untuk sekarang, kita bisa mengarahkannya ke implementasi baru
        // asalkan caller memastikan bahwa jika itu smart playlist, parameter playlistnya tidak null.
        return songIds.stream()
                      .map(this::getSongById)
                      .filter(java.util.Objects::nonNull)
                      .collect(Collectors.toList());
    }
    // --- AKHIR PERUBAHAN getSongsByIds ---

    public void addPlaylistToUser(User user, Playlist playlist) {
        user.addPlaylist(playlist);
        updateUserInList(user);
        System.out.println("Playlist '" + playlist.getName() + "' added to user '" + user.getUserName() + "' and saved.");
    }

    public void removePlaylistFromUser(User user, Playlist playlist) {
        user.removePlaylist(playlist);
        updateUserInList(user);
        System.out.println("Playlist '" + playlist.getName() + "' removed from user '" + user.getUserName() + "' and saved.");
    }

    public Playlist getPlaylistFromUser(User user, String playlistId) {
        return user.getPlaylists().stream()
                   .filter(p -> p.getId().equals(playlistId))
                   .findFirst()
                   .orElse(null);
    }
}