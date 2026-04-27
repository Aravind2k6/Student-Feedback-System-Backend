package com.feedback.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "form_fields")
public class FormField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId;

    // Client-facing id (e.g. "f1", "f2") within a form
    @JsonProperty("id")
    @Column(nullable = false)
    private String fieldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    @JsonIgnore
    private FeedbackForm form;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String label;

    @JsonProperty("type")
    @Column(nullable = false)
    private String fieldType; // "rating", "text", "textarea", "yesno", "select", "radio", "checkbox"

    private boolean required = true;

    // Stored as comma-separated string e.g. "Excellent,Good,Average,Poor"
    @Column(columnDefinition = "TEXT")
    private String options;

    private int sortOrder;

    public FormField() {}
    public FormField(Long dbId, String fieldId, FeedbackForm form, String label, String fieldType, boolean required, String options, int sortOrder) {
        this.dbId = dbId;
        this.fieldId = fieldId;
        this.form = form;
        this.label = label;
        this.fieldType = fieldType;
        this.required = required;
        this.options = options;
        this.sortOrder = sortOrder;
    }

    public Long getDbId() { return dbId; }
    public void setDbId(Long dbId) { this.dbId = dbId; }
    public String getFieldId() { return fieldId; }
    public void setFieldId(String fieldId) { this.fieldId = fieldId; }
    public FeedbackForm getForm() { return form; }
    public void setForm(FeedbackForm form) { this.form = form; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
