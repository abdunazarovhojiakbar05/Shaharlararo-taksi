package com.example.taxi_project.repository;

import com.example.taxi_project.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, UUID> {
}
