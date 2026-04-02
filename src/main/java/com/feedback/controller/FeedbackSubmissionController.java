package com.feedback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.dto.FeedbackSubmissionResponse;
import com.feedback.dto.SubmissionRequest;
import com.feedback.service.FeedbackSubmissionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
public class FeedbackSubmissionController {

    private final FeedbackSubmissionService submissionService;
    private final ObjectMapper objectMapper;

    public FeedbackSubmissionController(FeedbackSubmissionService submissionService, ObjectMapper objectMapper) {
        this.submissionService = submissionService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/check")
    public Map<String, Boolean> checkSubmission(@RequestParam String key) {
        return Map.of("submitted", submissionService.hasSubmitted(key));
    }

    @PostMapping
    public FeedbackSubmissionResponse submit(@Valid @RequestBody SubmissionRequest request) {
        return FeedbackSubmissionResponse.from(submissionService.submit(request), objectMapper);
    }

    @GetMapping
    public List<FeedbackSubmissionResponse> getAllSubmissions() {
        return submissionService.getAll().stream()
                .map(submission -> FeedbackSubmissionResponse.from(submission, objectMapper))
                .toList();
    }

    @GetMapping("/student/{studentId}")
    public List<FeedbackSubmissionResponse> getByStudent(@PathVariable Long studentId) {
        return submissionService.getByStudent(studentId).stream()
                .map(submission -> FeedbackSubmissionResponse.from(submission, objectMapper))
                .toList();
    }

    @GetMapping("/course/{course}")
    public List<FeedbackSubmissionResponse> getByCourse(@PathVariable String course) {
        return submissionService.getByCourse(course).stream()
                .map(submission -> FeedbackSubmissionResponse.from(submission, objectMapper))
                .toList();
    }

    @GetMapping("/instructor/{instructor}")
    public List<FeedbackSubmissionResponse> getByInstructor(@PathVariable String instructor) {
        return submissionService.getByInstructor(instructor).stream()
                .map(submission -> FeedbackSubmissionResponse.from(submission, objectMapper))
                .toList();
    }
}
