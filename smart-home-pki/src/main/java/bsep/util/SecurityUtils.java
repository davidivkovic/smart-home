package bsep.util;

import java.util.ArrayList;
import java.util.List;

public class SecurityUtils {

    static List<String> commonPasswords = new ArrayList<>();

    public static void init() {
        loadCommonPasswords();
    }

    public static void loadCommonPasswords() {
        try (var stream = SecurityUtils.class.getResourceAsStream("/1000-common-passwords.txt")) {
            if (stream != null) commonPasswords = List.of(new String(stream.readAllBytes()).split("\n"));
        }
        catch (Exception e) {
            System.out.println("Failed to load common passwords");
        }
    }

    public static boolean isCommonPassword(String password) {
        return commonPasswords.contains(password.toLowerCase());
    }

}
