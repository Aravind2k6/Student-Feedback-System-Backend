package com.feedback.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.entity.FeedbackSubmission;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

public class FeedbackSubmissionResponse {
    private Long id;
    private String submissionKey;
    private String formId;
    private Long studentId;
    private String studentName;
    private String course;
    private String instructor;
    private Integer rating;
    private Map<String, Object> dynamicRatings;
    private String remarks;
    private LocalDateTime submittedAt;

    public static FeedbackSubmissionResponse from(FeedbackSubmission submission, ObjectMapper objectMapper) {
        FeedbackSubmissionResponse response = new FeedbackSubmissionResponse();
        response.id = submission.getId();
        response.submissionKey = submission.getSubmissionKey();
        response.formId = submission.getFormId();
        
        // Null-safe student access to prevent 500 errors on orphaned submissions
        if (submission.getStudent() != null) {
            response.studentId = submission.getStudent().getId();
            response.studentName = submission.getStudent().getName();
        } else {
            response.studentId = null;
            response.studentName = "Deleted/Unknown Student";
        }

        response.course = submission.getCourse();
        response.instructor = submission.getInstructor();
        response.rating = submission.getOverallRating();
        response.remarks = submission.getRemarks();
        response.submittedAt = submission.getSubmittedAt();
        response.dynamicRatings = parseDynamicRatings(submission.getDynamicRatings(), objectMapper);
        return response;
    }

    private static Map<String, Object> parseDynamicRatings(String payload, ObjectMapper objectMapper) {
        if (payload == null || payload.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(payload, new TypeReference<>() {});
        } catch (Exception ignored) {
            return Collections.emptyMap();
        }
    }

    public Long getId() {
        return id;
    }

    public String getSubmissionKey() {
        return submissionKey;
    }

    public String getFormId() {
        return formId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCourse() {
        return course;
    }

    public String getInstructor() {
        return instructor;
    }

    public Integer getRating() {
        return rating;
    }

    public Map<String, Object> getDynamicRatings() {
        return dynamicRatings;
    }

    public String getRemarks() {
        return remarks;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}
