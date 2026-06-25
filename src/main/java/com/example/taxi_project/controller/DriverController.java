package com.example.taxi_project.controller;


import com.example.taxi_project.dto.driver.CarUpdate;
import com.example.taxi_project.dto.driver.DriverResponse;
import com.example.taxi_project.dto.driver.DriverUpdate;
import com.example.taxi_project.enums.DriverStatus;
import com.example.taxi_project.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/driver")
public class DriverController {

    private final DriverService driverService;

    @Operation(summary = "Haydovchi profil ma'lumotlarini olish")
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getProfile( @Valid @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(driverService.getById(userDetails));
    }


    @Operation(summary = "Haydovchi profilini tahrirlash")
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> updateProfile(
            @Valid
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DriverUpdate updateDto) {
        return ResponseEntity.ok(driverService.update(userDetails, updateDto));
    }


    @Operation(summary = "Haydovchi onlayn/oflayn statusini o'zgartirish")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @Valid
            @PathVariable UUID id,
            @RequestParam DriverStatus status) {
        driverService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Haydovchining joriy balansini ko'rish")
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance( @Valid @PathVariable UUID id) {
        return ResponseEntity.ok(driverService.getBalance(id));
    }


    @Operation(summary = "Haydovchiga tegishli transport vositasi ma'lumotlarini yangilash")
    @PutMapping("/{id}/car")
    public ResponseEntity<Void> updateCarInfo(
            @Valid
            @PathVariable UUID id,
            @RequestBody CarUpdate carUpdateDto) {
        driverService.updateCarInfo(id, carUpdateDto);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Haydovchining joriy reytingini olish")
    @GetMapping("/{id}/rating")
    public ResponseEntity<Double> getRating(@Valid  @PathVariable UUID id) {
        return ResponseEntity.ok(driverService.getRating(id));
    }
}