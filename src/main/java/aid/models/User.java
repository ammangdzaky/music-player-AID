package aid.models;
import javafx.scene.image.Image;

public class User {
    private String userName;
    private String fullName;
    private String password;
    private String profileImagePath; // simpan path gambar, bukan objek Image

    public User(String userName, String fullName, String password) {
        this.userName = userName;
        this.fullName = fullName;
        this.password = password;
    }

    public User(String userName, String fullName, String password, String profileImagePath) {
        this.userName = userName;
        this.fullName = fullName;
        this.password = password;
        this.profileImagePath = profileImagePath;
    }

    public String getUserName() { return userName; }
    public String getFullName() { return fullName; }
    public String getPassword() { return password; }
    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }
}