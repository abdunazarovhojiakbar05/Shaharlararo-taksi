package com.example.taxi_project.controller;

import com.example.taxi_project.dto.admin.DriverResponseDTO;
import com.example.taxi_project.service.DriverService;
import jakarta.validation.Valid;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/drivers")
@RequiredArgsConstructor
public class AdminController {

    private final DriverService driverService;

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DriverResponseDTO>> getPendingApplications() {
        return ResponseEntity.ok(driverService.getAllPendingApplications());
    }

    @PostMapping("/{userId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveDriver( @Valid @PathVariable UUID userId) throws ValidationException {
        driverService.approveDriver(userId);
        return ResponseEntity.ok("Haydovchi muvaffaqiyatli tasdiqlandi va rasmlar tozalandi.");
    }

    @PostMapping("/{userId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> rejectDriver( @Valid @PathVariable UUID userId, @RequestParam String reason) {
        driverService.rejectDriver(userId, reason);
        return ResponseEntity.ok("Ariza rad etildi.");
    }
}