package com.shopsphere.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for secure BCrypt password hashing and verification.
 */
public final class PasswordUtil {

    // Log rounds for BCrypt work factor (10 is secure and fast)
    private static final int BCRYPT_LOG_ROUNDS = 10;

    private PasswordUtil() {
        // Utility class
    }

    /**
     * Hashes a plain-text password using BCrypt with a secure salt.
     *
     * @param plainPassword user's clear text password
     * @return 60-character BCrypt hash
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    /**
     * Verifies whether a plain-text password matches a stored BCrypt hash.
     *
     * @param plainPassword user's input password
     * @param hashedPassword stored hash from the database
     * @return true if matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Malformed hash
            return false;
        }
    }
}
