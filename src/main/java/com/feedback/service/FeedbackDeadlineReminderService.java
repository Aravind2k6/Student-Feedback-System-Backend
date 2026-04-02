package com.feedback.service;

import com.feedback.entity.FeedbackForm;
import com.feedback.entity.FeedbackSubmission;
import com.feedback.entity.User;
import com.feedback.repository.FeedbackFormRepository;
import com.feedback.repository.FeedbackSubmissionRepository;
import com.feedback.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeedbackDeadlineReminderService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackDeadlineReminderService.class);

    private final FeedbackFormRepository formRepository;
    private final FeedbackSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final int reminderDaysBeforeDeadline;
    private final ZoneId reminderZoneId;

    public FeedbackDeadlineReminderService(
            FeedbackFormRepository formRepository,
            FeedbackSubmissionRepository submissionRepository,
            UserRepository userRepository,
            EmailService emailService,
            @Value("${app.feedback.deadline-reminder.days-before:1}") int reminderDaysBeforeDeadline,
            @Value("${app.feedback.deadline-reminder.zone:Asia/Kolkata}") String reminderZone) {
        this.formRepository = formRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.reminderDaysBeforeDeadline = Math.max(reminderDaysBeforeDeadline, 0);
        this.reminderZoneId = ZoneId.of(reminderZone);
    }

    @Transactional
    @Scheduled(
            cron = "${app.feedback.deadline-reminder.cron:0 0 9 * * *}",
            zone = "${app.feedback.deadline-reminder.zone:Asia/Kolkata}")
    public void sendPendingDeadlineReminderEmails() {
        LocalDate today = LocalDate.now(reminderZoneId);

        for (FeedbackForm form : formRepository.findByPublishedTrue()) {
            processReminder(form, today);
        }
    }

    private void processReminder(FeedbackForm form, LocalDate today) {
        if (form.getDeadlineReminderSentAt() != null) {
            return;
        }

        LocalDate deadline = parseDeadline(form);
        if (deadline == null) {
            return;
        }

        LocalDate reminderStartDate = deadline.minusDays(reminderDaysBeforeDeadline);
        if (today.isBefore(reminderStartDate) || today.isAfter(deadline)) {
            return;
        }

        var pendingRecipients = findPendingRecipientEmails(form.getId());
        if (pendingRecipients.isEmpty()) {
            form.setDeadlineReminderSentAt(LocalDateTime.now(reminderZoneId));
            formRepository.save(form);
            log.info("Skipping deadline reminder for form {} because all students have already submitted.", form.getId());
            return;
        }

        long daysRemaining = ChronoUnit.DAYS.between(today, deadline);
        int delivered = emailService.sendDeadlineReminderEmail(pendingRecipients, form, daysRemaining);
        if (delivered > 0) {
            form.setDeadlineReminderSentAt(LocalDateTime.now(reminderZoneId));
            formRepository.save(form);
            log.info("Sent {} deadline reminder email(s) for form {}.", delivered, form.getId());
            return;
        }

        log.warn("No deadline reminder emails were delivered for form {}. The job will retry on the next schedule.", form.getId());
    }

    private LocalDate parseDeadline(FeedbackForm form) {
        if (form.getDeadline() == null || form.getDeadline().isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(form.getDeadline().trim());
        } catch (DateTimeParseException ex) {
            log.warn("Skipping deadline reminder for form {} because deadline '{}' is invalid.", form.getId(), form.getDeadline());
            return null;
        }
    }

    private java.util.List<String> findPendingRecipientEmails(String formId) {
        Set<Long> submittedStudentIds = submissionRepository.findByFormId(formId).stream()
                .map(FeedbackSubmission::getStudent)
                .filter(Objects::nonNull)
                .map(User::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return userRepository.findByRole(User.Role.student).stream()
                .filter(student -> !submittedStudentIds.contains(student.getId()))
                .map(User::getEmail)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(email -> !email.isBlank())
                .distinct()
                .toList();
    }
}
