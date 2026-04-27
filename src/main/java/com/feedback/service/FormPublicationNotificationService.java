package com.feedback.service;

import com.feedback.entity.FeedbackForm;
import com.feedback.entity.User;
import com.feedback.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FormPublicationNotificationService {

    private static final Logger log = LoggerFactory.getLogger(FormPublicationNotificationService.class);

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public FormPublicationNotificationService(
            NotificationService notificationService,
            EmailService emailService,
            UserRepository userRepository) {
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Async("notificationTaskExecutor")
    public void dispatchPublishedFormNotifications(FeedbackForm form) {
        if (form == null || !form.isPublished()) {
            return;
        }

        try {
            notificationService.broadcast(
                    "new_campaign",
                    "New feedback form published: \"" + form.getTitle() + "\"",
                    "{\"formId\": \"" + form.getId() + "\"}");
        } catch (Exception ex) {
            log.error("Failed to broadcast in-app notifications for form {}.", form.getId(), ex);
        }

        try {
            int delivered = emailService.sendFormPublishedEmail(
                    userRepository.findByRole(User.Role.student).stream()
                            .map(User::getEmail)
                            .toList(),
                    form);
            log.info("Sent {} feedback launch email(s) for form {}.", delivered, form.getId());
        } catch (Exception ex) {
            log.error("Failed to send publication emails for form {}.", form.getId(), ex);
        }
    }
}
