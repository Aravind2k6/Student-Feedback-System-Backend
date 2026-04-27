package com.feedback.dto;

import com.feedback.entity.Notification;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private String type;
    private String message;
    private String metadata;
    private LocalDateTime timestamp;
    private boolean read;

    public static NotificationResponse from(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.id = notification.getId();
        response.type = notification.getType();
        response.message = notification.getMessage();
        response.metadata = notification.getMetadata();
        response.timestamp = notification.getTimestamp();
        response.read = notification.isRead();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getMetadata() {
        return metadata;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }
}
