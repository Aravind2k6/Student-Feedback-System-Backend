package com.feedback.controller;

import com.feedback.dto.DashboardStatsResponse;
import com.feedback.dto.UserResponse;
import com.feedback.repository.FeedbackFormRepository;
import com.feedback.repository.FeedbackSubmissionRepository;
import com.feedback.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final FeedbackFormRepository formRepository;
    private final FeedbackSubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public AdminDashboardController(FeedbackFormRepository formRepository,
                                    FeedbackSubmissionRepository submissionRepository,
                                    UserRepository userRepository) {
        this.formRepository = formRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    public DashboardStatsResponse getStats() {
        long totalForms = formRepository.count();
        long totalSubmissions = submissionRepository.count();
        long totalUsers = userRepository.count();
        long publishedForms = formRepository.findByPublishedTrue().size();
        return new DashboardStatsResponse(totalForms, totalSubmissions, totalUsers, publishedForms);
    }

    @GetMapping("/users")
    public java.util.List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }
}
