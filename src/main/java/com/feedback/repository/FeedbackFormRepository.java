package com.feedback.repository;

import com.feedback.entity.FeedbackForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackFormRepository extends JpaRepository<FeedbackForm, String> {
    List<FeedbackForm> findByPublishedTrue();
}
