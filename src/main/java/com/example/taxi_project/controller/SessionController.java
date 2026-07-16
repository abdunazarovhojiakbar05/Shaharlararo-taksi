package com.example.taxi_project.controller;

import com.example.taxi_project.dto.session.SessionResponse;
import com.example.taxi_project.security.CustomUserDetails;
import com.example.taxi_project.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("getAll")
    public ResponseEntity<List<SessionResponse>> getSessions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(sessionService.getMySessions(userDetails, authHeader));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revoke(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        sessionService.revokeSession(id, userDetails);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/others")
    public ResponseEntity<Void> revokeOthers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader("Authorization") String authHeader) {
        sessionService.revokeAllOtherSessions(userDetails, authHeader);
        return ResponseEntity.ok().build();
    }
}
