package com.feedback.controller;

import com.feedback.dto.FormCreateRequest;
import com.feedback.dto.MessageResponse;
import com.feedback.entity.FeedbackForm;
import com.feedback.service.FeedbackFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
@Tag(name = "Feedback Forms", description = "Endpoints for managing feedback forms")
public class FeedbackFormController {

    private final FeedbackFormService formService;

    public FeedbackFormController(FeedbackFormService formService) {
        this.formService = formService;
    }

    @GetMapping
    @Operation(summary = "Get all feedback forms", description = "Retrieves a list of all existing feedback forms.")
    public List<FeedbackForm> getAllForms() {
        return formService.getAllForms();
    }

    @GetMapping("/published")
    @Operation(summary = "Get published forms", description = "Retrieves a list of all published feedback forms ready for student submission.")
    public List<FeedbackForm> getPublishedForms() {
        return formService.getPublishedForms();
    }

    @PostMapping
    @Operation(summary = "Create a new form", description = "Creates a new feedback form with specific courses and instructors.")
    public FeedbackForm createForm(@Valid @RequestBody FormCreateRequest request) {
        return formService.createForm(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a form", description = "Deletes a specific feedback form by its ID.")
    public MessageResponse deleteForm(@PathVariable String id) {
        formService.deleteForm(id);
        return new MessageResponse("Form deleted successfully");
    }

    @DeleteMapping("/reset-all")
    @Operation(summary = "Reset all data", description = "CRITICAL: Deletes all forms and submissions to reset the system.")
    public MessageResponse resetAllData() {
        formService.resetAllData();
        return new MessageResponse("System reset successful. All forms and submissions have been cleared.");
    }
}
