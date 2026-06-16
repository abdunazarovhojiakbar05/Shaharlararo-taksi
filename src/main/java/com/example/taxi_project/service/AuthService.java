package com.example.taxi_project.service;

import com.example.taxi_project.dto.auth.*;

public interface AuthService {
    void data(Long telegramId, String firstName, String lastName, String username, String code);

    String sendCode(String phone);

    void logout(String token);

    RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto dto);

    SendOtpResponse registration(RegistrationRequestDto requestDto);

    LoginResponseDto verifyOtpCode(VerifyOtpRequest requestDto);

    SendOtpResponse login(SendOtpRequest requestDto);
}
