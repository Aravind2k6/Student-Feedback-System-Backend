package com.feedback.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class SubmissionRequest {
    @NotBlank(message = "Submission key is required.")
    private String submissionKey;

    @NotBlank(message = "Form id is required.")
    private String formId;

    @NotNull(message = "Student id is required.")
    private Long studentId;

    @NotBlank(message = "Course is required.")
    private String course;

    @NotBlank(message = "Instructor is required.")
    private String instructor;

    @NotNull(message = "Rating is required.")
    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 5, message = "Rating must be at most 5.")
    private Integer rating;
    private Map<String, Object> dynamicRatings;
    private String remarks;

    public SubmissionRequest() {}

    public String getSubmissionKey() { return submissionKey; }
    public void setSubmissionKey(String submissionKey) { this.submissionKey = submissionKey; }
    public String getFormId() { return formId; }
    public void setFormId(String formId) { this.formId = formId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public Map<String, Object> getDynamicRatings() { return dynamicRatings; }
    public void setDynamicRatings(Map<String, Object> dynamicRatings) { this.dynamicRatings = dynamicRatings; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
