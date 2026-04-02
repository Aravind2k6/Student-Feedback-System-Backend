package com.feedback.controller;

import com.feedback.dto.FormCreateRequest;
import com.feedback.dto.MessageResponse;
import com.feedback.entity.FeedbackForm;
import com.feedback.service.FeedbackFormService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class FeedbackFormController {

    private final FeedbackFormService formService;

    public FeedbackFormController(FeedbackFormService formService) {
        this.formService = formService;
    }

    @GetMapping
    public List<FeedbackForm> getAllForms() {
        return formService.getAllForms();
    }

    @GetMapping("/published")
    public List<FeedbackForm> getPublishedForms() {
        return formService.getPublishedForms();
    }

    @PostMapping
    public FeedbackForm createForm(@Valid @RequestBody FormCreateRequest request) {
        return formService.createForm(request);
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteForm(@PathVariable String id) {
        formService.deleteForm(id);
        return new MessageResponse("Form deleted successfully");
    }
}
