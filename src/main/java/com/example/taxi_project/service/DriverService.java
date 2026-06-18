package com.example.taxi_project.service;

import com.example.taxi_project.dto.admin.DriverResponseDTO;
import com.example.taxi_project.dto.driver.CarUpdate;
import com.example.taxi_project.dto.driver.DriverResponse;
import com.example.taxi_project.dto.driver.DriverUpdate;
import com.example.taxi_project.enums.DriverStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DriverService {
    DriverResponse getById(UUID id);

    DriverResponse update(UUID id, DriverUpdate updateDto);


    void changeStatus(UUID id, DriverStatus status);

    BigDecimal getBalance(UUID id);

    void updateCarInfo(UUID id, CarUpdate carUpdateDto);

    Double getRating(UUID id);

    void apply(long chatId, Map<String, String> data);

    List<DriverResponseDTO> getAllPendingApplications();

    void approveDriver(Long userId);

    void rejectDriver(Long userId, String reason);
}
