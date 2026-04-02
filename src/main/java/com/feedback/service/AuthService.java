package com.feedback.service;

import com.feedback.dto.LoginRequest;
import com.feedback.dto.RegisterRequest;
import com.feedback.entity.PasswordResetToken;
import com.feedback.entity.User;
import com.feedback.repository.PasswordResetTokenRepository;
import com.feedback.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService,
            NotificationService notificationService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Optional<User> login(LoginRequest request) {
        String identifier = request.getIdentifier().trim().toLowerCase();
        String password = request.getPassword().trim();
        User.Role role = resolveRole(request.getRole());

        Optional<User> userOpt = userRepository.findByUsernameAndRole(identifier, role);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmailAndRole(identifier, role);
        }

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        return matchesPassword(user, password) ? Optional.of(user) : Optional.empty();
    }

    @Transactional
    public User register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String username = request.getUsername().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        user.setRole(resolveRole(request.getRole()));

        User saved = userRepository.save(user);
        notificationService.notifyUser(
                saved.getId(),
                "account",
                "Welcome to the Student Feedback System.",
                "{\"event\":\"account_created\"}");
        return saved;
    }

    @Transactional
    public void forgotPassword(String email) {
        passwordResetTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        String cleanEmail = email.trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(cleanEmail);
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        passwordResetTokenRepository.deleteByUser_Id(user.getId());

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        passwordResetTokenRepository.save(token);

        emailService.sendResetEmail(cleanEmail, token.getToken());
        notificationService.notifyUser(
                user.getId(),
                "security",
                "A password reset link was generated for your account.",
                "{\"event\":\"password_reset_requested\"}");
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token."));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.deleteByUser_Id(resetToken.getUser().getId());
            throw new IllegalArgumentException("Invalid or expired reset token.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword.trim()));
        userRepository.save(user);
        passwordResetTokenRepository.deleteByUser_Id(user.getId());

        notificationService.notifyUser(
                user.getId(),
                "security",
                "Your password was updated successfully.",
                "{\"event\":\"password_reset_completed\"}");
    }

    private User.Role resolveRole(String role) {
        return "admin".equalsIgnoreCase(role) ? User.Role.admin : User.Role.student;
    }

    private boolean matchesPassword(User user, String rawPassword) {
        String storedPassword = user.getPassword();
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (looksLikeBcrypt(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }

        boolean matchesLegacyPassword = rawPassword.equals(storedPassword.trim());
        if (matchesLegacyPassword) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
        }
        return matchesLegacyPassword;
    }

    private boolean looksLikeBcrypt(String password) {
        return password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$");
    }
}
