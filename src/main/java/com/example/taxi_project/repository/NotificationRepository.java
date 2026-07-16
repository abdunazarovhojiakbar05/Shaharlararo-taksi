package com.example.taxi_project.repository;

import com.example.taxi_project.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    @Query("SELECT n FROM notifications n WHERE n.user_id = :userId ORDER BY n.created_at DESC")
    List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    @Query("SELECT n FROM notifications n WHERE n.driver_id = :driverId ORDER BY n.created_at DESC")
    List<Notification> findByDriverIdOrderByCreatedAtDesc(@Param("driverId") UUID driverId);
}
