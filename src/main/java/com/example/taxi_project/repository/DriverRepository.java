package com.example.taxi_project.repository;

import com.example.taxi_project.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {

    Optional<Driver> findDriverByPhone(String phone);

    boolean existsByPhone(String phone);

    @Query("SELECT d FROM drivers  d WHERE d.phone = :phone")
    Optional<Driver> findByPhone(@Param("phone") String phone);


    @Query("SELECT d FROM drivers d WHERE d.id = :userId")
    Optional<Driver> findByUserId(@Param("userId") UUID userId);}
