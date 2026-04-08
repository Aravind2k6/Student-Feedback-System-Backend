package com.feedback.repository;

import com.feedback.entity.FeedbackSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FeedbackSubmissionRepository extends JpaRepository<FeedbackSubmission, Long> {
    
    @Query("SELECT s FROM FeedbackSubmission s JOIN FETCH s.student")
    List<FeedbackSubmission> findAllWithStudent();

    @Override
    @Query("SELECT s FROM FeedbackSubmission s JOIN FETCH s.student")
    List<FeedbackSubmission> findAll();

    Optional<FeedbackSubmission> findBySubmissionKey(String submissionKey);
    boolean existsBySubmissionKey(String submissionKey);
    List<FeedbackSubmission> findByStudentId(Long studentId);
    List<FeedbackSubmission> findByFormId(String formId);
    List<FeedbackSubmission> findByCourse(String course);
    List<FeedbackSubmission> findByInstructor(String instructor);
}
