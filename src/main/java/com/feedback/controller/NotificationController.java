package com.feedback.controller;

import com.feedback.dto.MessageResponse;
import com.feedback.dto.NotificationResponse;
import com.feedback.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> getNotifications(@RequestParam Long userId) {
        return notificationService.getForUser(userId).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @PatchMapping("/read-all")
    public MessageResponse markAllRead(@RequestParam Long userId) {
        notificationService.markAllRead(userId);
        return new MessageResponse("All notifications marked as read");
    }

    @DeleteMapping
    public MessageResponse clearNotifications(@RequestParam Long userId) {
        notificationService.clearUserNotifications(userId);
        return new MessageResponse("Notifications cleared");
    }
}
