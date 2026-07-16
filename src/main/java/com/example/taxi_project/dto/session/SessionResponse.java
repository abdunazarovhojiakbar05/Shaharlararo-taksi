package com.example.taxi_project.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class SessionResponse {
    private UUID id;
    private String platform;
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean current;
}
