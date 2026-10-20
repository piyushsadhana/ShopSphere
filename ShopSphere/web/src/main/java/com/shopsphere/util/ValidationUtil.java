package com.shopsphere.util;

import java.util.regex.Pattern;

/**
 * Utility for server-side form validation and input sanitization.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+?\\d{1,4}[\\s-]?)?\\(?\\d{3}\\)?[\\s-]?\\d{3}[\\s-]?\\d{4}$"
    );

    private ValidationUtil() {
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Phone is optional in some contexts
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        // At least 6 characters
        return password.length() >= 6;
    }

    /**
     * Sanitizes input strings against basic XSS attacks.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;")
                    .replace("/", "&#x2F;")
                    .trim();
    }
}
