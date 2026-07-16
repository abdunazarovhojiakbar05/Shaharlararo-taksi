package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.session.SessionResponse;
import com.example.taxi_project.enums.SessionStatus;
import com.example.taxi_project.exceptions.ResourceNotFoundException;
import com.example.taxi_project.exceptions.ValidationException;
import com.example.taxi_project.model.Session;
import com.example.taxi_project.repository.SessionRepository;
import com.example.taxi_project.security.CustomUserDetails;
import com.example.taxi_project.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    public List<SessionResponse> getMySessions(CustomUserDetails userDetails, String currentAccessToken) {
        String token = cleanToken(currentAccessToken);

        List<Session> sessions = userDetails.isParent()
                ? sessionRepository.findByUserIdAndIsActiveTrue(userDetails.getUser().getId())
                : sessionRepository.findByDriverIdAndIsActiveTrue(userDetails.getDriver().getId());

        return sessions.stream()
                .map(s -> SessionResponse.builder()
                        .id(s.getId())
                        .platform(s.getPlatform() != null ? s.getPlatform().name() : null)
                        .ipAddress(s.getIpAddress())
                        .createdAt(s.getCreatedAt())
                        .expiresAt(s.getExpiresAt())
                        .current(token.equals(s.getAccessToken()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void revokeSession(UUID sessionId, CustomUserDetails userDetails) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("session.not.found"));

        UUID ownerId = userDetails.isParent() ? userDetails.getUser().getId() : userDetails.getDriver().getId();
        UUID sessionOwnerId = session.getUser() != null ? session.getUser().getId() : session.getDriver().getId();

        if (!ownerId.equals(sessionOwnerId)) {
            throw new ValidationException("Bu sessiya sizga tegishli emas");
        }

        revoke(session);
    }

    @Transactional
    @Override
    public void revokeAllOtherSessions(CustomUserDetails userDetails, String currentAccessToken) {
        String token = cleanToken(currentAccessToken);

        List<Session> sessions = userDetails.isParent()
                ? sessionRepository.findByUserIdAndIsActiveTrue(userDetails.getUser().getId())
                : sessionRepository.findByDriverIdAndIsActiveTrue(userDetails.getDriver().getId());

        for (Session session : sessions) {
            if (!token.equals(session.getAccessToken())) {
                revoke(session);
            }
        }
    }

    private void revoke(Session session) {
        session.setIsActive(false);
        session.setSessionStatus(SessionStatus.expired);
        session.setAccessToken(null);
        session.setRefreshToken(null);
        session.setRevokedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    private String cleanToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}