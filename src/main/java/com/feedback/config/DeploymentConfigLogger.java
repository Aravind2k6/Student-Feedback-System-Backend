package com.feedback.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DeploymentConfigLogger implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DeploymentConfigLogger.class);

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173,http://localhost:3000,http://127.0.0.1:3000}")
    private String allowedOriginsValue;

    @Value("${app.cors.allowed-origin-patterns:}")
    private String allowedOriginPatternsValue;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Override
    public void run(ApplicationArguments args) {
        List<String> allowedOrigins = splitCsv(allowedOriginsValue);
        List<String> allowedOriginPatterns = splitCsv(allowedOriginPatternsValue);

        log.info(
                "Deployment config loaded: frontendBaseUrl={}, mailEnabled={}, mailHostConfigured={}, mailUsernameConfigured={}, mailPasswordConfigured={}, allowedOrigins={}, allowedOriginPatterns={}",
                frontendBaseUrl,
                mailEnabled,
                !mailHost.isBlank(),
                !mailUsername.isBlank(),
                !mailPassword.isBlank(),
                allowedOrigins,
                allowedOriginPatterns
        );

        if (mailEnabled && (mailHost.isBlank() || mailUsername.isBlank() || mailPassword.isBlank())) {
            log.warn("Mail is enabled but SMTP configuration is incomplete. Set MAIL_HOST, MAIL_PORT, MAIL_USERNAME, and MAIL_PASSWORD on the deployed backend.");
        }

        if (isLocalhost(frontendBaseUrl)) {
            log.warn("APP_FRONTEND_BASE_URL is still pointing to localhost. Password reset links will not work correctly in production.");
        }

        boolean hasNonLocalOrigin = allowedOrigins.stream().anyMatch(origin -> !isLocalhost(origin));
        if (!hasNonLocalOrigin && allowedOriginPatterns.isEmpty()) {
            log.warn("Only localhost CORS origins are configured. Set APP_CORS_ALLOWED_ORIGINS or APP_CORS_ALLOWED_ORIGIN_PATTERNS for your deployed frontend.");
        }
    }

    private List<String> splitCsv(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }

    private boolean isLocalhost(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.trim().toLowerCase();
        return normalized.contains("localhost") || normalized.contains("127.0.0.1");
    }
}
