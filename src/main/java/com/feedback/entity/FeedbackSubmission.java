package com.feedback.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback_submissions")
public class FeedbackSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique submission key used to prevent duplicate submissions
    // Pattern: "fb-<formId>-<course>-<instructor>"
    @Column(nullable = false, unique = true)
    private String submissionKey;

    @Column(nullable = false)
    private String formId;

    // Student who submitted
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private User student;

    @Column(nullable = false)
    private String course;

    @Column(nullable = false)
    private String instructor;

    private Integer overallRating; // 1–4

    // Stores the per-field ratings as JSON: {"f1":3,"f2":4,...}
    @Column(columnDefinition = "TEXT")
    private String dynamicRatings;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    public FeedbackSubmission() {}
    public FeedbackSubmission(Long id, String submissionKey, String formId, User student, String course, String instructor, Integer overallRating, String dynamicRatings, String remarks, LocalDateTime submittedAt) {
        this.id = id;
        this.submissionKey = submissionKey;
        this.formId = formId;
        this.student = student;
        this.course = course;
        this.instructor = instructor;
        this.overallRating = overallRating;
        this.dynamicRatings = dynamicRatings;
        this.remarks = remarks;
        this.submittedAt = submittedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSubmissionKey() { return submissionKey; }
    public void setSubmissionKey(String submissionKey) { this.submissionKey = submissionKey; }
    public String getFormId() { return formId; }
    public void setFormId(String formId) { this.formId = formId; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public Integer getOverallRating() { return overallRating; }
    public void setOverallRating(Integer overallRating) { this.overallRating = overallRating; }
    public String getDynamicRatings() { return dynamicRatings; }
    public void setDynamicRatings(String dynamicRatings) { this.dynamicRatings = dynamicRatings; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
