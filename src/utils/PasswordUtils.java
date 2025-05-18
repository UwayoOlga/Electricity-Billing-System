package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

     public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

     public static boolean check(String providedPassword, String storedHash) {
        if (storedHash == null) {
            return false;
        }
        return BCrypt.checkpw(providedPassword, storedHash);
    }

}
