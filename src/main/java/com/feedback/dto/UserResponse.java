package com.feedback.dto;

import com.feedback.entity.User;

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String username;
    private String role;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.name = user.getName();
        response.email = user.getEmail();
        response.username = user.getUsername();
        response.role = user.getRole().name();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
