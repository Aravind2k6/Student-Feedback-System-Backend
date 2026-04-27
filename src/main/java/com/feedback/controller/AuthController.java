package com.feedback.controller;

import com.feedback.dto.ApiErrorResponse;
import com.feedback.dto.ForgotPasswordRequest;
import com.feedback.dto.LoginRequest;
import com.feedback.dto.MessageResponse;
import com.feedback.dto.RegisterRequest;
import com.feedback.dto.ResetPasswordRequest;
import com.feedback.dto.UserResponse;
import com.feedback.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request)
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(UserResponse.from(user)))
                .orElseGet(() -> ResponseEntity.status(401).body(new ApiErrorResponse("Invalid credentials")));
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return UserResponse.from(authService.register(request));
    }

    @PostMapping("/forgot-password")
    public MessageResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return new MessageResponse("If an account with that email exists, a reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return new MessageResponse("Password has been reset successfully.");
    }
}
