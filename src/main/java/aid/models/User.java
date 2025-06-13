package aid.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userName;
    private String nickName;
    private String password;
    private String profileImagePath;
    // PENTING: Jangan inisialisasi langsung di sini jika Gson menginisialisasi ulang.
    // Biarkan saja sebagai deklarasi. Inisialisasi akan ditangani di konstruktor atau setter.
    private List<Playlist> playlists; // <--- Hapus '= new ArrayList<>();' di sini

    // Konstruktor utama
    public User(String userName, String nickName, String password) {
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
        this.profileImagePath = "/images/default_avatar.jpg";
        this.playlists = new ArrayList<>(); // <--- INISIALISASI DI KONSTRUKTOR
    }

    // Konstruktor dengan profileImagePath
    public User(String userName, String nickName, String password, String profileImagePath) {
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
        this.profileImagePath = profileImagePath;
        this.playlists = new ArrayList<>(); // <--- INISIALISASI DI KONSTRUKTOR
    }

    // --- Getters and Setters ---
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public List<Playlist> getPlaylists() {
        // <--- PERBAIKAN KRITIS DI SINI
        // Pastikan 'playlists' tidak pernah null saat diakses.
        if (this.playlists == null) {
            this.playlists = new ArrayList<>();
        }
        return new ArrayList<>(this.playlists); // Selalu kembalikan salinan
    }

    public void setPlaylists(List<Playlist> playlists) {
        // <--- PERBAIKAN KRITIS DI SINI
        // Jika input 'playlists' null, inisialisasi dengan daftar kosong.
        this.playlists = playlists != null ? new ArrayList<>(playlists) : new ArrayList<>();
    }

    public void addPlaylist(Playlist playlist) {
        // Pastikan 'playlists' tidak null sebelum menambah
        if (this.playlists == null) {
            this.playlists = new ArrayList<>();
        }
        if (playlist != null && !this.playlists.contains(playlist)) {
            this.playlists.add(playlist);
        }
    }

    public void removePlaylist(Playlist playlist) {
        if (this.playlists != null) { // Pastikan tidak null sebelum menghapus
            this.playlists.remove(playlist);
        }
    }
    
    public Playlist getPlaylistByName(String name) {
        if (this.playlists == null) { // Pastikan tidak null sebelum iterasi
            return null;
        }
        for (Playlist p : playlists) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        // Handle null playlists gracefully for toString
        int playlistCount = (playlists != null) ? playlists.size() : 0;
        return "User{" +
               "userName='" + userName + '\'' +
               ", nickName='" + nickName + '\'' +
               ", profileImagePath='" + profileImagePath + '\'' +
               ", playlists=" + playlistCount + " playlists" +
               '}';
    }
}