package com.feedback.repository;

import com.feedback.entity.FeedbackSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackSubmissionRepository extends JpaRepository<FeedbackSubmission, Long> {
    Optional<FeedbackSubmission> findBySubmissionKey(String submissionKey);
    boolean existsBySubmissionKey(String submissionKey);
    List<FeedbackSubmission> findByStudentId(Long studentId);
    List<FeedbackSubmission> findByFormId(String formId);
    List<FeedbackSubmission> findByCourse(String course);
    List<FeedbackSubmission> findByInstructor(String instructor);
}
