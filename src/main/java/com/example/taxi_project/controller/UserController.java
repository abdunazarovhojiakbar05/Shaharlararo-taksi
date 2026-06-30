package com.example.taxi_project.controller;


import com.example.taxi_project.dto.order.MyOrdersResponse;
import com.example.taxi_project.dto.user.DriverApplyRequest;
import com.example.taxi_project.dto.user.UserResponse;
import com.example.taxi_project.dto.user.UserUpdate;
import com.example.taxi_project.security.CustomUserDetails;
import com.example.taxi_project.service.OrderService;
import com.example.taxi_project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final UserService userService;
    private     final OrderService orderService;


    @Operation(summary = "Mijozning profil ma'lumotlarini olish")
    @GetMapping()
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getById(userDetails.getUser().getId()));
    }

    @Operation(summary = "Profil ma'lumotlarini tahrirlash")
    @PutMapping()
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserUpdate updateDto) {
        return ResponseEntity.ok(userService.update(userDetails.getUser().getId(), updateDto));
    }


    @Operation(summary = "Mijoz akkauntini o'chirish")
    @DeleteMapping()
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.delete(userDetails.getUser().getId());
        return ResponseEntity.noContent().build();
    }



}