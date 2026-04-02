package com.feedback.repository;

import com.feedback.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_IdOrderByTimestampDesc(Long userId);
    List<Notification> findByUserIsNullOrderByTimestampDesc();

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllReadForUser(@Param("userId") Long userId);

    void deleteByUser_Id(Long userId);
}
