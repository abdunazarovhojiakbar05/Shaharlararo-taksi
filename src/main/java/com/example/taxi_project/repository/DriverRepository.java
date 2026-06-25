package com.example.taxi_project.repository;

import com.example.taxi_project.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {

    Optional<Driver> findDriverByPhone(String phone);
}
