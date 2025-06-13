package aid.utils;

import java.util.UUID;

public class IDGenerator {
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}