package com.example.taxi_project.service.impl;

import com.example.taxi_project.exceptions.ValidationException;
import com.example.taxi_project.model.Notification;
import com.example.taxi_project.repository.NotificationRepository;
import com.example.taxi_project.security.CustomUserDetails;
import com.example.taxi_project.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void create(UUID recipientId, boolean isDriver, String title, String message) {
        Notification notification = Notification.builder()
                .user_id(isDriver ? null : recipientId)
                .driver_id(isDriver ? recipientId : null) // "driver_id" deb o'zgartirilgandan keyin shu nom ham mos yangilanadi
                .title(title)
                .message(message)
                .is_read(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getMyNotifications(CustomUserDetails userDetails) {
        return userDetails.isParent()
                ? notificationRepository.findByUserIdOrderByCreatedAtDesc(userDetails.getUser().getId())
                : notificationRepository.findByDriverIdOrderByCreatedAtDesc(userDetails.getDriver().getId());
    }

    @Transactional
    @Override
    public void markAsRead(UUID notificationId, CustomUserDetails userDetails) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ValidationException("Bildirishnoma topilmadi"));

        UUID ownerId = userDetails.isParent() ? userDetails.getUser().getId() : userDetails.getDriver().getId();
        UUID notifOwnerId = notification.getUser_id() != null ? notification.getUser_id() : notification.getDriver_id();

        if (!ownerId.equals(notifOwnerId)) {
            throw new ValidationException("Bu bildirishnoma sizga tegishli emas");
        }

        notification.setIs_read(true);
        notificationRepository.save(notification);
    }
}