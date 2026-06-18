package com.example.taxi_project.model;




import com.example.taxi_project.enums.Platform;
import com.example.taxi_project.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = true)
    Driver driver;

    @Column(unique = true)
    String accessToken;

    @Column(unique = true)
    String refreshToken;

    String ipAddress;

    @Enumerated(EnumType.STRING)
    Platform platform;

    @Enumerated(EnumType.STRING)
    SessionStatus sessionStatus = SessionStatus.active;

    LocalDateTime expiresAt;
    LocalDateTime createdAt;
    LocalDateTime revokedAt;

    Boolean isActive = true;
}