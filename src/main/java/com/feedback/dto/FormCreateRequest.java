package com.feedback.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class FormCreateRequest {
    @NotBlank(message = "Title is required.")
    private String title;
    private String description;

    @NotBlank(message = "Deadline is required.")
    private String deadline;
    private boolean published = true;

    @NotBlank(message = "Form type is required.")
    @Pattern(regexp = "^(Course|Institution)$", message = "Type must be 'Course' or 'Institution'.")
    private String type;   // "Course" | "Institution"

    @NotBlank(message = "Target audience is required.")
    private String target; // course full name or "All Students"
    private String course; // short code e.g. "FSAD"
    @Valid
    @NotEmpty(message = "At least one field is required.")
    private List<FieldDto> fields;

    public FormCreateRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public List<FieldDto> getFields() { return fields; }
    public void setFields(List<FieldDto> fields) { this.fields = fields; }

    public static class FieldDto {
        @NotBlank(message = "Field id is required.")
        private String id;        // "f1", "f2" ...
        @NotBlank(message = "Field label is required.")
        private String label;
        @NotBlank(message = "Field type is required.")
        private String type;      // "rating", "text", etc.
        private boolean required;
        private List<String> options;

        public FieldDto() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
    }
}
