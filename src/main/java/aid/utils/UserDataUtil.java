package aid.utils;

import aid.models.User;
import aid.managers.DataManager;
import java.util.List;

public class UserDataUtil {
    // Gunakan DataManager sebagai satu-satunya sumber kebenasan untuk data
    // Jangan inisialisasi langsung di sini. Biarkan null dan inisialisasi secara lazy.
    private static DataManager dataManagerInstance; 

    // Constructor private untuk mencegah instansiasi
    private UserDataUtil() {
        // utility class
    }

    // Getter untuk DataManager instance dengan lazy initialization
    public static DataManager getDataManager() {
        if (dataManagerInstance == null) {
            System.out.println("DEBUG: DataManager instance is null. Attempting lazy initialization.");
            synchronized (UserDataUtil.class) { // Pastikan thread-safe jika ada multi-threading
                if (dataManagerInstance == null) {
                    try {
                        dataManagerInstance = new DataManager();
                        System.out.println("DEBUG: DataManager successfully lazy initialized.");
                    } catch (Exception e) {
                        System.err.println("CRITICAL ERROR: Failed to initialize DataManager. Application might be unstable.");
                        e.printStackTrace();
                        // Ini adalah titik kritis. Jika DataManager gagal diinisialisasi,
                        // aplikasi mungkin tidak bisa berfungsi. Anda bisa memilih untuk
                        // melemparkan RuntimeException atau menampilkan Alert di sini.
                        throw new RuntimeException("Failed to load application data. Please check data files.", e);
                    }
                }
            }
        }
        return dataManagerInstance;
    }

    // Semua metode di bawah ini akan memanggil getDataManager() terlebih dahulu
    
    public static List<User> loadUsers() {
        getDataManager().loadUsers();
        return getDataManager().getUsers();
    }

    public static void updateUser(String oldUserName, User updatedUser) {
        User existingUser = getDataManager().getUserByUsername(oldUserName);
        if (existingUser != null) {
            existingUser.setUserName(updatedUser.getUserName());
            existingUser.setNickName(updatedUser.getNickName());
            existingUser.setProfileImagePath(updatedUser.getProfileImagePath());
            existingUser.setPlaylists(updatedUser.getPlaylists());
            getDataManager().updateUserInList(existingUser);
        } else {
            System.err.println("User with old username " + oldUserName + " not found for update.");
        }
    }

    public static void saveUsers(List<User> users) { // Metode ini mungkin tidak lagi diperlukan
        getDataManager().saveUsers(); 
    }

    public static void addUser(User user) {
        getDataManager().addUser(user);
    }

    public static User findUser(String userName, String password) {
        getDataManager().loadUsers(); 
        User foundUser = getDataManager().getUserByUsername(userName);
        if (foundUser != null && foundUser.getPassword().equals(password)) {
            return foundUser;
        }
        return null;
    }

    public static boolean isUsernameTaken(String userName) {
        getDataManager().loadUsers();
        return getDataManager().isUsernameTaken(userName);
    }
}