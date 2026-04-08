package com.feedback.config;

import com.feedback.entity.Course;
import com.feedback.entity.FeedbackForm;
import com.feedback.entity.FormField;
import com.feedback.entity.User;
import com.feedback.repository.CourseRepository;
import com.feedback.repository.FeedbackFormRepository;
import com.feedback.repository.NotificationRepository;
import com.feedback.repository.UserRepository;
import com.feedback.service.NotificationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    @ConditionalOnProperty(name = "app.bootstrap.seed.enabled", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner seedData(
            UserRepository userRepo,
            CourseRepository courseRepo,
            FeedbackFormRepository formRepo,
            NotificationRepository notificationRepository,
            NotificationService notificationService,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (userRepo.count() == 0) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@feedback.com");
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(User.Role.admin);
                userRepo.save(admin);

                User student = new User();
                student.setName("Demo Student");
                student.setEmail("student@feedback.com");
                student.setUsername("student");
                student.setPassword(passwordEncoder.encode("student123"));
                student.setRole(User.Role.student);
                userRepo.save(student);
            }

            if (courseRepo.count() == 0) {
                courseRepo.saveAll(List.of(
                        new Course(null, "FSAD", "24SDC02E", "Full Stack Application Development", "Ramu", 4, true),
                        new Course(null, "CIS", "24CS220A", "Cloud Infrastructure and Services", "Ganesh", 3, true),
                        new Course(null, "DBMS", "24DBMS301", "Database Management Systems", "Abhinav", 4, true),
                        new Course(null, "OS", "24OSS401", "Operating Systems", "Raghavendra", 3, true),
                        new Course(null, "AIML", "24AML501", "Artificial Intelligence", "Sai", 3, true)
                ));
            }

            if (formRepo.count() == 0) {
                formRepo.save(buildSeedForm(
                        "campaign-seed-1",
                        "FSAD Course & Instructor Evaluation",
                        "Provide feedback on course quality and instructor performance.",
                        "2026-02-20T10:00:00",
                        "2026-03-24",
                        "Course", "Full Stack Application Development", "FSAD"
                ));

                formRepo.save(buildSeedForm(
                        "campaign-seed-2",
                        "Course Quality Evaluation - DBMS",
                        "End of semester course quality evaluation for Database Management Systems.",
                        "2026-02-01T10:00:00",
                        "2026-03-25",
                        "Course", "Database Management Systems", "DBMS"
                ));

                formRepo.save(buildSeedForm(
                        "campaign-seed-4",
                        "Institutional Services Feedback",
                        "Share your experience with institutional support and services.",
                        "2026-02-05T10:00:00",
                        "2026-03-30",
                        "Institution", "All Students", null
                ));

                formRepo.save(buildSeedForm(
                        "campaign-seed-5",
                        "Advanced Feedback Survey - OS",
                        "Comprehensive evaluation for Operating Systems.",
                        "2026-02-20T10:00:00",
                        "2026-04-15",
                        "Course", "Operating Systems", "OS"
                ));
            }

            if (notificationRepository.count() == 0) {
                notificationService.broadcast(
                        "new_campaign",
                        "New feedback form published: \"Mid-Semester Course Feedback\"",
                        "{\"formId\":\"campaign-seed-1\"}");
                notificationService.broadcast(
                        "alert",
                        "Reminder: The \"End-Semester Evaluation\" deadline is approaching!",
                        "{\"formId\":\"campaign-seed-2\"}");
            }
        };
    }

    private FeedbackForm buildSeedForm(String id, String title, String description,
                                       String createdAtStr, String deadline,
                                       String type, String target, String course) {
        FeedbackForm form = new FeedbackForm();
        form.setId(id);
        form.setTitle(title);
        form.setDescription(description);
        form.setCreatedAt(LocalDateTime.parse(createdAtStr));
        form.setDeadline(deadline);
        form.setPublished(true);
        form.setType(type);
        form.setTarget(target);
        form.setCourse(course);

        String[] labels = {
                "How well did the instructor explain the subject concepts?",
                "How clear were the lecture presentations?",
                "How useful were the study materials provided for the subject?",
                "How effectively were doubts and questions addressed during the class?",
                "Overall, how would you rate this subject?"
        };
        String[][] options = {
                {"Excellent", "Good", "Average", "Poor"},
                {"Very Clear", "Clear", "Somewhat Clear", "Not Clear"},
                {"Very Useful", "Useful", "Slightly Useful", "Not Useful"},
                {"Very Effectively", "Effectively", "Moderately", "Not Effectively"},
                {"Excellent", "Good", "Average", "Poor"}
        };

        List<FormField> fields = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            FormField field = new FormField();
            field.setFieldId("f" + (i + 1));
            field.setForm(form);
            field.setLabel(labels[i]);
            field.setFieldType("rating");
            field.setRequired(true);
            field.setOptions(String.join(",", options[i]));
            field.setSortOrder(i);
            fields.add(field);
        }
        form.setFields(fields);
        return form;
    }
}
