package com.feedback.config;

import com.feedback.entity.Notification;
import com.feedback.entity.User;
import com.feedback.repository.NotificationRepository;
import com.feedback.repository.PasswordResetTokenRepository;
import com.feedback.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseMaintenanceRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMaintenanceRunner.class);

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseMaintenanceRunner(
            UserRepository userRepository,
            NotificationRepository notificationRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) {
        migrateLegacyPasswords();
        migrateLegacyBroadcastNotifications();
        removeExpiredResetTokens();
        dropLegacyResetColumns();
    }

    private void migrateLegacyPasswords() {
        List<User> dirtyUsers = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            if (user.getPassword() != null && !looksLikeBcrypt(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
                dirtyUsers.add(user);
            }
        }
        if (!dirtyUsers.isEmpty()) {
            userRepository.saveAll(dirtyUsers);
            log.info("Migrated {} legacy plaintext password(s) to BCrypt.", dirtyUsers.size());
        }
    }

    private void migrateLegacyBroadcastNotifications() {
        List<Notification> legacyBroadcasts = notificationRepository.findByUserIsNullOrderByTimestampDesc();
        if (legacyBroadcasts.isEmpty()) {
            return;
        }

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return;
        }

        List<Notification> replacements = new ArrayList<>();
        for (Notification legacy : legacyBroadcasts) {
            for (User user : users) {
                Notification notification = new Notification();
                notification.setType(legacy.getType());
                notification.setMessage(legacy.getMessage());
                notification.setMetadata(legacy.getMetadata());
                notification.setTimestamp(legacy.getTimestamp());
                notification.setRead(false);
                notification.setUser(user);
                replacements.add(notification);
            }
        }

        notificationRepository.saveAll(replacements);
        notificationRepository.deleteAll(legacyBroadcasts);
        log.info("Migrated {} legacy broadcast notification(s) into {} user-scoped notification(s).",
                legacyBroadcasts.size(), replacements.size());
    }

    private void removeExpiredResetTokens() {
        long deleted = passwordResetTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Removed {} expired password reset token(s).", deleted);
        }
    }

    private void dropLegacyResetColumns() {
        dropColumnIfExists("users", "reset_token");
        dropColumnIfExists("users", "reset_token_expiry");
    }

    private void dropColumnIfExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND column_name = ?
                """, Integer.class, tableName, columnName);

        if (count != null && count > 0) {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
            log.info("Dropped legacy column {}.{}.", tableName, columnName);
        }
    }

    private boolean looksLikeBcrypt(String password) {
        return password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$");
    }
}
