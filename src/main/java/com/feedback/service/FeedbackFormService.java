package com.feedback.service;

import com.feedback.dto.FormCreateRequest;
import com.feedback.entity.FeedbackForm;
import com.feedback.entity.FormField;
import com.feedback.entity.User;
import com.feedback.repository.FeedbackFormRepository;
import com.feedback.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackFormService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackFormService.class);

    private final FeedbackFormRepository formRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public FeedbackFormService(
            FeedbackFormRepository formRepository,
            NotificationService notificationService,
            EmailService emailService,
            UserRepository userRepository) {
        this.formRepository = formRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    public List<FeedbackForm> getAllForms() {
        return formRepository.findAll();
    }

    public List<FeedbackForm> getPublishedForms() {
        return formRepository.findByPublishedTrue();
    }

    public FeedbackForm createForm(FormCreateRequest request) {
        FeedbackForm form = new FeedbackForm();
        form.setId("form-" + System.currentTimeMillis());
        form.setTitle(request.getTitle());
        form.setDescription(request.getDescription());
        form.setCreatedAt(LocalDateTime.now());
        form.setDeadline(request.getDeadline());
        form.setPublished(request.isPublished());
        form.setType(request.getType());
        form.setTarget(request.getTarget());
        form.setCourse(request.getCourse());

        List<FormField> fields = new ArrayList<>();
        if (request.getFields() != null) {
            for (int i = 0; i < request.getFields().size(); i++) {
                FormCreateRequest.FieldDto dto = request.getFields().get(i);
                FormField field = new FormField();
                field.setFieldId(dto.getId());
                field.setForm(form);
                field.setLabel(dto.getLabel());
                field.setFieldType(dto.getType());
                field.setRequired(dto.isRequired());
                if (dto.getOptions() != null) {
                    field.setOptions(String.join(",", dto.getOptions()));
                }
                field.setSortOrder(i);
                fields.add(field);
            }
        }
        form.setFields(fields);
        FeedbackForm saved = formRepository.save(form);

        // Broadcast notification
        if (saved.isPublished()) {
            notificationService.broadcast("new_campaign",
                    "New feedback form published: \"" + saved.getTitle() + "\"",
                    "{\"formId\": \"" + saved.getId() + "\"}");
            int delivered = emailService.sendFormPublishedEmail(
                    userRepository.findByRole(User.Role.student).stream()
                            .map(User::getEmail)
                            .toList(),
                    saved);
            log.info("Sent {} feedback launch email(s) for form {}.", delivered, saved.getId());
        }

        return saved;
    }

    public void deleteForm(String id) {
        formRepository.deleteById(id);
    }
}
