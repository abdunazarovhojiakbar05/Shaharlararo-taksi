package com.example.taxi_project.controller;


import com.example.taxi_project.dto.user.UserResponse;
import com.example.taxi_project.dto.user.UserUpdate;
import com.example.taxi_project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;


    @Operation(summary = "Mijozning profil ma'lumotlarini olish")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Operation(summary = "Profil ma'lumotlarini tahrirlash")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable UUID id,
            @RequestBody UserUpdate updateDto) {
        return ResponseEntity.ok(userService.update(id, updateDto));
    }


    @Operation(summary = "Mijozning joriy hamyon qoldig'ini tekshirish")
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getBalance(id));
    }


    @Operation(summary = "Hamyonni to'ldirish")
    @PostMapping("/{id}/balance/top-up")
    public ResponseEntity<Void> topUpBalance(
            @PathVariable UUID id,
            @RequestParam BigDecimal amount) {
        userService.topUpBalance(id, amount);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Mijoz akkauntini o'chirish")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}