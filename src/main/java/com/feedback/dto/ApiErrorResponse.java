package com.feedback.dto;

import java.time.LocalDateTime;

public class ApiErrorResponse {
    private final String error;
    private final LocalDateTime timestamp;

    public ApiErrorResponse(String error) {
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    public String getError() {
        return error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
