package com.example.taxi_project.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.taxi_project.model.Session;
import com.example.taxi_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository  extends JpaRepository<Session, UUID> {
    Optional<Session> findByAccessToken(String token);

    Optional<Object> findByAccessTokenAndIsActiveTrue(String token);

    Optional<Object> findByRefreshTokenAndIsActiveTrue(String refreshToken);

    Optional<Object> findByUserAndIsActiveTrue(User user);

    Optional<Session> findByRefreshToken(String refreshToken);

    List<Session> findByUserIdAndIsActiveTrue(UUID userId);

    List<Session> findByDriverIdAndIsActiveTrue(UUID driverId);


}
