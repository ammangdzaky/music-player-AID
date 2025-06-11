package aid.utils;
import aid.models.User;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class UserDataUtil {
    private static final String USER_FILE = "data/users.json";
    private static final Gson gson = new Gson();

    public static List<User> loadUsers() {
        try {
            if (!Files.exists(Paths.get(USER_FILE))) return new ArrayList<>();
            Reader reader = Files.newBufferedReader(Paths.get(USER_FILE));
            Type listType = new TypeToken<List<User>>(){}.getType();
            List<User> users = gson.fromJson(reader, listType);
            reader.close();
            return users != null ? users : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveUsers(List<User> users) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE))) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }

    public static User findUser(String nickName, String password) {
        for (User user : loadUsers()) {
            if (user.getUserName().equals(nickName) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
