package com.example.taxi_project.controller;


import com.example.taxi_project.dto.driver.CarUpdate;
import com.example.taxi_project.dto.driver.DriverResponse;
import com.example.taxi_project.dto.driver.DriverUpdate;
import com.example.taxi_project.enums.DriverStatus;
import com.example.taxi_project.security.CustomUserDetails;
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
    @GetMapping()
    public ResponseEntity<DriverResponse> getProfile( @Valid @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(driverService.getById(userDetails));
    }


    @Operation(summary = "Haydovchi profilini tahrirlash")
    @PutMapping()
    public ResponseEntity<DriverResponse> updateProfile(
            @Valid
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody DriverUpdate updateDto) {
        return ResponseEntity.ok(driverService.update(userDetails, updateDto));
    }



    @PatchMapping("/status")
    public ResponseEntity<Void> changeStatus(
            @Valid
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam DriverStatus status) {
        driverService.changeStatus(userDetails, status);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Haydovchining joriy balansini ko'rish")
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance( @Valid @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(driverService.getBalance(userDetails));
    }


    @Operation(summary = "Haydovchiga tegishli transport vositasi ma'lumotlarini yangilash")
    @PutMapping("/car")
    public ResponseEntity<Void> updateCarInfo(
            @Valid
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CarUpdate carUpdateDto) {
        driverService.updateCarInfo(userDetails.getDriver().getId(), carUpdateDto);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Haydovchining joriy reytingini olish")
    @GetMapping("/rating")
    public ResponseEntity<Double> getRating(@Valid  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(driverService.getRating(userDetails.getDriver().getId()));
    }
}