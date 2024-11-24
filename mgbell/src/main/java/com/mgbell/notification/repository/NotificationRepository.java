package com.mgbell.notification.repository;

import com.mgbell.notification.model.entity.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationHistory, Long> {
}
