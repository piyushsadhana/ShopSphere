package com.shopsphere.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordUtilTest {

    @Test
    @DisplayName("Verify BCrypt hashing and verification")
    public void testHashAndCheck() {
        String adminPass = "Admin@123";
        String customerPass = "Customer@123";

        String adminHash = PasswordUtil.hashPassword(adminPass);
        String customerHash = PasswordUtil.hashPassword(customerPass);

        System.out.println("ADMIN_HASH=" + adminHash);
        System.out.println("CUSTOMER_HASH=" + customerHash);

        assertNotNull(adminHash);
        assertTrue(PasswordUtil.checkPassword(adminPass, adminHash));
        assertFalse(PasswordUtil.checkPassword("WrongPassword", adminHash));

        assertNotNull(customerHash);
        assertTrue(PasswordUtil.checkPassword(customerPass, customerHash));
        assertFalse(PasswordUtil.checkPassword("WrongPass", customerHash));
    }
}
