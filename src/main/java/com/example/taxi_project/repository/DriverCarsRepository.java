package com.example.taxi_project.repository;

import com.example.taxi_project.model.Cars;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverCarsRepository extends JpaRepository<Cars, UUID> {
}
