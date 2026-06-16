package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.auth.*;
import com.example.taxi_project.enums.UserRole;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.UserRepository;
import com.example.taxi_project.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public void data(Long telegramId, String firstName, String lastName, String username, String code) {

        User user = new User();
        user.setUsername(username);
        user.setRole(UserRole.USER);

        userRepository.save(user);

    }

    @Override
    public String sendCode(String phone) {

        User user = new User();
        user.setPhone(phone);
        userRepository.save(user);

        return "user saqlandi! ";
    }

    @Override
    public void logout(String token) {

    }

    @Override
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto dto) {
        return null;
    }

    @Override
    public SendOtpResponse registration(RegistrationRequestDto requestDto) {
        return null;
    }

    @Override
    public LoginResponseDto verifyOtpCode(VerifyOtpRequest requestDto) {
        return null;
    }

    @Override
    public SendOtpResponse login(SendOtpRequest requestDto) {
        return null;
    }
}
