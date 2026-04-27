package com.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {
    @NotBlank(message = "Username or email is required.")
    private String identifier; // username or email

    @NotBlank(message = "Password is required.")
    private String password;

    @NotBlank(message = "Role is required.")
    @Pattern(regexp = "^(student|admin)$", message = "Role must be 'student' or 'admin'.")
    private String role;       // "admin" | "student"

    public LoginRequest() {}

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
