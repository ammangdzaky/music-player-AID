package aid.models;
import javafx.scene.image.Image;

public class User {
    private String userName;
    private String nickName;
    private String password;
    private String profileImagePath; // simpan path gambar, bukan objek Image

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

    public String getUserName() { return userName; }
    public String getNickName() { return nickName; }
    public String getPassword() { return password; }
    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }
}