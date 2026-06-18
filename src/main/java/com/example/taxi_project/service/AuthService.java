package com.example.taxi_project.service;

import com.example.taxi_project.dto.auth.*;
import com.example.taxi_project.model.User;
import jakarta.xml.bind.ValidationException;

public interface AuthService {
    User data(String firstName, String username, String code, Long chatId) throws ValidationException;

    void logout(String token);

    RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto dto);

    SendOtpResponse registration(RegistrationRequestDto requestDto);

    LoginResponseDto verifyOtpCode(VerifyOtpRequest requestDto);

 }
