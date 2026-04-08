package com.feedback.service;

import com.feedback.entity.FeedbackForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final boolean mailEnabled;
    private final boolean notificationEmailsEnabled;
    private final String frontendBaseUrl;
    private final String mailHost;
    private final String mailUsername;
    private final String mailPassword;
    private final String mailFrom;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.enabled:false}") boolean mailEnabled,
            @Value("${app.mail.notifications.enabled:false}") boolean notificationEmailsEnabled,
            @Value("${app.frontend.base-url:http://localhost:5173}") String frontendBaseUrl,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${spring.mail.username:}") String mailUsername,
            @Value("${spring.mail.password:}") String mailPassword,
            @Value("${app.mail.from:}") String mailFrom) {
        this.mailSender = mailSender;
        this.mailEnabled = mailEnabled;
        this.notificationEmailsEnabled = notificationEmailsEnabled;
        this.frontendBaseUrl = frontendBaseUrl;
        this.mailHost = mailHost;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
        this.mailFrom = mailFrom;
    }

    public boolean sendResetEmail(String email, String token) {
        String resetLink = normalizeBaseUrl(frontendBaseUrl) + "/reset-password?token=" + token;
        String subject = "Password Reset Request";
        String body = """
                We received a password reset request for your Student Feedback account.

                Reset your password using the link below:
                %s

                This link expires in 60 minutes.
                If you did not request this, you can safely ignore this email.
                """.formatted(resetLink);

        return sendEmail(email, subject, body, true);
    }

    public int sendFormPublishedEmail(List<String> recipients, FeedbackForm form) {
        String subject = "Feedback Form Launched: " + defaultValue(form.getTitle(), "Student Feedback");
        String body = """
                A new feedback form has been launched by the admin.

                Title: %s
                Deadline: %s
                Category: %s

                Submit your feedback here:
                %s
                """.formatted(
                defaultValue(form.getTitle(), "Student Feedback"),
                defaultValue(form.getDeadline(), "Not specified"),
                defaultValue(form.getType(), "General"),
                normalizeBaseUrl(frontendBaseUrl));

        return sendBulkEmail(recipients, subject, body);
    }

    public int sendDeadlineReminderEmail(List<String> recipients, FeedbackForm form, long daysRemaining) {
        String subject = "Reminder: Feedback Deadline for " + defaultValue(form.getTitle(), "Student Feedback");
        String deadlineMessage = daysRemaining <= 0
                ? "The submission deadline is today."
                : "The submission deadline is in " + daysRemaining + " day(s).";
        String body = """
                This is a reminder to submit your feedback form before the deadline.

                Title: %s
                Deadline: %s
                %s

                Submit your feedback here:
                %s
                """.formatted(
                defaultValue(form.getTitle(), "Student Feedback"),
                defaultValue(form.getDeadline(), "Not specified"),
                deadlineMessage,
                normalizeBaseUrl(frontendBaseUrl));

        return sendBulkEmail(recipients, subject, body);
    }

    private int sendBulkEmail(List<String> recipients, String subject, String body) {
        if (!notificationEmailsEnabled || recipients == null || recipients.isEmpty()) {
            return 0;
        }

        int delivered = 0;
        for (String recipient : recipients.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(email -> !email.isBlank())
                .distinct()
                .toList()) {
            if (sendEmail(recipient, subject, body, false)) {
                delivered++;
            }
        }
        return delivered;
    }

    private boolean sendEmail(String recipient, String subject, String body, boolean important) {
        if (!isMailConfigured()) {
            log.warn("Mail is disabled or incomplete. Skipping email to {}. enabled={}, hostConfigured={}, usernameConfigured={}, passwordConfigured={}",
                    recipient,
                    mailEnabled,
                    !mailHost.isBlank(),
                    !mailUsername.isBlank(),
                    !mailPassword.isBlank());
            if (important) {
                log.info("Requested email subject='{}' body='{}'", subject, body);
            }
            return false;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        if (!mailFrom.isBlank()) {
            message.setFrom(mailFrom);
        }

        try {
            mailSender.send(message);
            log.info("Email sent successfully to {}", recipient);
            return true;
        } catch (Exception ex) {
            log.error("Failed to send email to {}: [{}] {}", recipient, ex.getClass().getSimpleName(), ex.getMessage());
            if (important) {
                log.error("Full troubleshooting trace for email to {}:", recipient, ex);
            }
            return false;
        }
    }

    private boolean isMailConfigured() {
        return mailEnabled
                && !mailHost.isBlank()
                && !mailUsername.isBlank()
                && !mailPassword.isBlank();
    }

    private String normalizeBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            return "http://localhost:5173";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
