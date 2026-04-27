package com.feedback.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.dto.SubmissionRequest;
import com.feedback.entity.FeedbackSubmission;
import com.feedback.entity.User;
import com.feedback.repository.FeedbackSubmissionRepository;
import com.feedback.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackSubmissionService {

    private final FeedbackSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public FeedbackSubmissionService(FeedbackSubmissionRepository submissionRepository,
                                     UserRepository userRepository,
                                     NotificationService notificationService,
                                     ObjectMapper objectMapper) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    public boolean hasSubmitted(String submissionKey) {
        return submissionRepository.existsBySubmissionKey(submissionKey);
    }

    public FeedbackSubmission submit(SubmissionRequest request) {
        if (hasSubmitted(request.getSubmissionKey())) {
            throw new IllegalStateException("Feedback already submitted for key: " + request.getSubmissionKey());
        }

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + request.getStudentId()));

        FeedbackSubmission submission = new FeedbackSubmission();
        submission.setSubmissionKey(request.getSubmissionKey());
        submission.setFormId(request.getFormId());
        submission.setStudent(student);
        submission.setCourse(request.getCourse());
        submission.setInstructor(request.getInstructor());
        submission.setOverallRating(request.getRating());
        submission.setRemarks(request.getRemarks());
        submission.setSubmittedAt(LocalDateTime.now());

        // Serialize dynamic ratings map to JSON string
        try {
            if (request.getDynamicRatings() != null) {
                submission.setDynamicRatings(objectMapper.writeValueAsString(request.getDynamicRatings()));
            }
        } catch (Exception e) {
            submission.setDynamicRatings("{}");
        }

        FeedbackSubmission saved = submissionRepository.save(submission);
        notificationService.notifyUser(
                student.getId(),
                "submission",
                "Feedback submitted successfully for " + saved.getCourse() + ".",
                "{\"formId\":\"" + saved.getFormId() + "\"}");
        return saved;
    }

    public List<FeedbackSubmission> getAll() {
        return submissionRepository.findAll();
    }

    public List<FeedbackSubmission> getByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    public List<FeedbackSubmission> getByCourse(String course) {
        return submissionRepository.findByCourse(course);
    }

    public List<FeedbackSubmission> getByInstructor(String instructor) {
        return submissionRepository.findByInstructor(instructor);
    }
}
