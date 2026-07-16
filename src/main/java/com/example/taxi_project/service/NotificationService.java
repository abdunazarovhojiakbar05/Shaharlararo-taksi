package com.example.taxi_project.service;

import com.example.taxi_project.model.Notification;
import com.example.taxi_project.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void create(UUID recipientId, boolean isDriver, String title, String message);
    List<Notification> getMyNotifications(CustomUserDetails userDetails);
    void markAsRead(UUID notificationId, CustomUserDetails userDetails);
}
