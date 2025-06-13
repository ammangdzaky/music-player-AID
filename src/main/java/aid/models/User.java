package aid.models;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

public class User {
    private String userName;
    private String nickName;
    private String password;
    private String profileImagePath; // simpan path gambar, bukan objek Image
    private List<Playlist> playlists = new ArrayList<>();

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public User(String userName, String nickName, String password) {
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
    }

    public User(String userName, String nickName, String password, String profileImagePath) {
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
        this.profileImagePath = profileImagePath;
    }

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

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public class Playlist {
        private String name;
        private String description;
        private List<Song> songs;

    }

}