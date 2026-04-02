package com.feedback.service;

import com.feedback.entity.Notification;
import com.feedback.entity.User;
import com.feedback.repository.NotificationRepository;
import com.feedback.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Notification> getForUser(Long userId) {
        return notificationRepository.findByUser_IdOrderByTimestampDesc(userId);
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllReadForUser(userId);
    }

    @Transactional
    public void clearUserNotifications(Long userId) {
        notificationRepository.deleteByUser_Id(userId);
    }

    @Transactional
    public List<Notification> broadcast(String type, String message, String metadata) {
        List<User> recipients = userRepository.findAll();
        if (recipients.isEmpty()) {
            return List.of();
        }

        List<Notification> notifications = new ArrayList<>();
        for (User recipient : recipients) {
            notifications.add(buildNotification(recipient, type, message, metadata));
        }
        return notificationRepository.saveAll(notifications);
    }

    @Transactional
    public Notification notifyUser(Long userId, String type, String message, String metadata) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return notificationRepository.save(buildNotification(user, type, message, metadata));
    }

    private Notification buildNotification(User user, String type, String message, String metadata) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setMessage(message);
        notification.setMetadata(metadata);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        notification.setUser(user);
        return notification;
    }
}
