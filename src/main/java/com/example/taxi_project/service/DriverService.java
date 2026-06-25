package com.example.taxi_project.service;

import com.example.taxi_project.dto.admin.DriverResponseDTO;
import com.example.taxi_project.dto.driver.CarUpdate;
import com.example.taxi_project.dto.driver.DriverResponse;
import com.example.taxi_project.dto.driver.DriverUpdate;
import com.example.taxi_project.enums.DriverStatus;
import com.example.taxi_project.security.CustomUserDetails;
import jakarta.xml.bind.ValidationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DriverService {
    DriverResponse getById(UserDetails userDetails);

    DriverResponse update(UserDetails userDetails, DriverUpdate updateDto);


    void changeStatus(CustomUserDetails id, DriverStatus status);

    BigDecimal getBalance(CustomUserDetails id);

    void updateCarInfo(UUID id, CarUpdate carUpdateDto);

    Double getRating(UUID id);

    void apply(long chatId, Map<String, String> data);

    List<DriverResponseDTO> getAllPendingApplications();

    void approveDriver(UUID userId) throws ValidationException;

    void rejectDriver(UUID userId, String reason);
}
