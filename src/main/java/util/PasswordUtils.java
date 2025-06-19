package util;

public class PasswordUtils {
    private static final int SHIFT = 3;

    public static String encrypt(String input) {
        StringBuilder encrypted = new StringBuilder();
        for (char ch : input.toCharArray()) {
            // Shift letters and digits only
            if (Character.isLetterOrDigit(ch)) {
                encrypted.append((char) (ch + SHIFT));
            } else {
                encrypted.append(ch); // Leave symbols unchanged
            }
        }
        return encrypted.toString();
    }

    public static String decrypt(String input) {
        StringBuilder decrypted = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (Character.isLetterOrDigit(ch)) {
                decrypted.append((char) (ch - SHIFT));
            } else {
                decrypted.append(ch);
            }
        }
        return decrypted.toString();
    }
}
