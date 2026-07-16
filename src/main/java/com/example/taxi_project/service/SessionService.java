package com.example.taxi_project.service;

import com.example.taxi_project.dto.session.SessionResponse;
import com.example.taxi_project.security.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface SessionService {
    List<SessionResponse> getMySessions(CustomUserDetails userDetails, String currentAccessToken);
    void revokeSession(UUID sessionId, CustomUserDetails userDetails);
    void revokeAllOtherSessions(CustomUserDetails userDetails, String currentAccessToken);
}
