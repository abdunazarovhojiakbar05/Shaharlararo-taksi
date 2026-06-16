package com.example.taxi_project.controller;


import com.example.taxi_project.dto.auth.*;
import com.example.taxi_project.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autentifikatsiya", description = "Tizimga kirish va ro'yxatdan o'tish API-lari")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Ota-ona login send", description = "Ota-ona tizimga kirishi (kod olishi) uchun faqat email manzilini yuboradi.")
    @PostMapping("/send")
    public ResponseEntity<SendOtpResponse> send(@Valid @RequestBody SendOtpRequest requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }


    @Operation(summary = "Ota-ona login verify", description = "Ota-ona emailga kelgan maxfiy kod bilan emailni yuboradi va tizimga kiradi.")
    @PostMapping("/verify")
    public ResponseEntity<LoginResponseDto> verify(@Valid @RequestBody VerifyOtpRequest requestDto) {
        return ResponseEntity.ok(authService.verifyOtpCode(requestDto));
    }




    @Operation(summary = "Ota-ona ro'yxatdan o'tishi", description = "Yangi ota-ona akkaunti yaratadi va qurilma (device) ma'lumotlarini tizimda saqlaydi.")
    @PostMapping("/registration")
    public ResponseEntity<SendOtpResponse> registration(@Valid @RequestBody RegistrationRequestDto requestDto) {
        return ResponseEntity.ok(authService.registration(requestDto));
    }

    @Operation(summary = "Refresh token", description = "Eski yoki muddati tugayotgan refresh token orqali yangi access token va refresh token oladi.")
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto dto) {
        RefreshTokenResponseDto response = authService.refreshToken(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Tizimdan chiqish", description = "Foydalanuvchining faol tokenini bekor qiladi.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authHeader) throws BadRequestException {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Token mavjud emas");
        }

        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok("Tizimdan muvaffaqiyatli chiqildi!");
    }
}