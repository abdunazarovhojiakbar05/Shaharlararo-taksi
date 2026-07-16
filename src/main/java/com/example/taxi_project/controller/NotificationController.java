package com.example.taxi_project.controller;


import com.example.taxi_project.model.Notification;
import com.example.taxi_project.security.CustomUserDetails;
import com.example.taxi_project.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(notificationService.getMyNotifications(userDetails));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails userDetails){
        notificationService.markAsRead(id, userDetails);
        return ResponseEntity.ok().build();
    }
}
