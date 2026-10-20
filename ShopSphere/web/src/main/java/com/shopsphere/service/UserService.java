package com.shopsphere.service;

import com.shopsphere.dao.UserDAO;
import com.shopsphere.model.User;
import com.shopsphere.util.PasswordUtil;
import com.shopsphere.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service handling User registration, authentication, and profile management.
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new customer account.
     * Validates input, prevents duplicate email, and hashes password with BCrypt.
     */
    public User register(String name, String email, String password, String phone) throws IllegalArgumentException, SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (userDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email address already exists.");
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setPhone(phone != null ? phone.trim() : null);
        user.setRole(User.Role.CUSTOMER); // Public registration is strictly CUSTOMER
        user.setStatus(User.Status.ACTIVE);

        return userDAO.create(user);
    }

    /**
     * Authenticates a user with email and password.
     */
    public Optional<User> authenticate(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }

        Optional<User> userOpt = userDAO.findByEmail(email.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.isActive()) {
                return Optional.empty(); // Account deactivated or suspended
            }
            if (PasswordUtil.checkPassword(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> findById(int userId) {
        return userDAO.findById(userId);
    }

    public boolean updateProfile(int userId, String name, String phone) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        User user = new User();
        user.setUserId(userId);
        user.setName(name.trim());
        user.setPhone(phone != null ? phone.trim() : null);
        return userDAO.updateProfile(user);
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) throws IllegalArgumentException {
        if (!ValidationUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("New password must be at least 6 characters.");
        }
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        if (!PasswordUtil.checkPassword(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        String hashedNew = PasswordUtil.hashPassword(newPassword);
        return userDAO.updatePassword(userId, hashedNew);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public int countCustomers() {
        return userDAO.countUsers();
    }

    public boolean updateStatus(int userId, User.Status status) {
        return userDAO.updateStatus(userId, status);
    }
}
