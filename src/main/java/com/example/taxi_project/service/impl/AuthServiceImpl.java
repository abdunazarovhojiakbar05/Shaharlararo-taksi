package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.auth.*;
import com.example.taxi_project.enums.Platform;
import com.example.taxi_project.enums.SessionStatus;
import com.example.taxi_project.enums.UserRole;
import com.example.taxi_project.exceptions.ResourceNotFoundException;
import com.example.taxi_project.model.Session;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.SessionRepository;
import com.example.taxi_project.repository.UserRepository;
import com.example.taxi_project.service.AuthService;
import com.example.taxi_project.util.JwtUtils;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final SessionRepository sessionRepository;

    @Override
    public User data(String firstName, String username, String code, Long chatId, String phone) throws ValidationException {
        System.out.println("Kod: " + code);
        if (code == null) throw new ValidationException("Kod bo'sh bo'lmasligi kerak");

        User user = userRepository.findByChatId(chatId).orElseGet(() -> {
            User newUser = new User();
            newUser.setPhone(phone);
            newUser.setName(firstName);
            newUser.setUsername(username);
            newUser.setChat_id(chatId);
            newUser.setRole(UserRole.USER);
            return newUser;
        });

        user.setCode(code);
        user.setExpired_at(LocalDateTime.now().plusMinutes(5));
        user.setActive(false);

        return userRepository.save(user);
    }



    @Override
    public LoginResponseDto verifyOtpCode(VerifyOtpRequest requestDto) {

        System.out.println("Kod: " + requestDto.getCode());

        User user = userRepository.findByCode(requestDto.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Kod noto'g'ri"));


        if (user.getExpired_at() == null || user.getExpired_at().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Kod muddati o'tib ketgan");
        }

        user.setActive(true);
        user.setCode("");
        user.setExpired_at(null);
        userRepository.save(user);

        String access = jwtUtils.generateToken(user.getPhone());
        String refresh = jwtUtils.generateRefreshToken(user.getPhone());


        Session session = Session.builder()
                .user(user)
                .driver(null)
                .refreshToken(refresh)
                .accessToken(access)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .createdAt(LocalDateTime.now())
                .platform(Platform.android)
                .build();
        sessionRepository.save(session);

        return LoginResponseDto.builder()
                .name(user.getName())
                .access_token(access)
                .refresh_token(refresh)
                .build();
    }



    @Override
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto dto) {
        String refreshToken = dto.getRefresh_token();

        if (!jwtUtils.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token yaroqsiz yoki muddati o'tgan");
        }

        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("session.not.found"));

        String phone = jwtUtils.getUsernameFromToken(refreshToken);
        String newAccess = jwtUtils.generateToken(phone);
        String newRefresh = jwtUtils.generateRefreshToken(phone);

        session.setAccessToken(newAccess);
        session.setRefreshToken(newRefresh);
        session.setExpiresAt(LocalDateTime.now().plusDays(1));
        sessionRepository.save(session);

        return RefreshTokenResponseDto.builder()
                .refresh_token(newRefresh)
                .access_token(newAccess)
                .expires_in(900L)
                .build();
    }

    @Override
    public void logout(String token) {

        if (token == null) {
            throw new  ResourceNotFoundException("data.not.found");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Session session = sessionRepository.findByAccessToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("session.not.found"));
        session.setIsActive(false);
        session.setSessionStatus(SessionStatus.expired);
        session.setAccessToken(null);
        session.setRefreshToken(null);
        session.setRevokedAt(LocalDateTime.now());
        sessionRepository.save(session);



    }
}