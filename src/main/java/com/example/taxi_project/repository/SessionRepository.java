package com.example.taxi_project.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.taxi_project.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepository  extends JpaRepository<Session, UUID> {
    Optional<Session> findByAccessToken(String token);
}
