package com.feedback.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "feedback_forms")
public class FeedbackForm {

    @Id
    @Column(nullable = false)
    private String id;   // "form-<timestamp>" or seed IDs

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private String deadline; // stored as date string "YYYY-MM-DD"

    private boolean published = true;
    private LocalDateTime deadlineReminderSentAt;

    private String type;   // "Course" | "Institution"
    private String target; // "Full Stack Application Development" | "All Students"
    private String course; // short code e.g. "FSAD"

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sortOrder ASC")
    private List<FormField> fields = new ArrayList<>();

    public FeedbackForm() {}
    public FeedbackForm(String id, String title, String description, LocalDateTime createdAt, String deadline, boolean published, String type, String target, String course, List<FormField> fields) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.published = published;
        this.type = type;
        this.target = target;
        this.course = course;
        this.fields = fields;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public LocalDateTime getDeadlineReminderSentAt() { return deadlineReminderSentAt; }
    public void setDeadlineReminderSentAt(LocalDateTime deadlineReminderSentAt) { this.deadlineReminderSentAt = deadlineReminderSentAt; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public List<FormField> getFields() { return fields; }
    public void setFields(List<FormField> fields) { this.fields = fields; }
}
