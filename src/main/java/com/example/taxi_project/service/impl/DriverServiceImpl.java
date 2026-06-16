package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.driver.CarUpdate;
import com.example.taxi_project.dto.driver.DriverResponse;
import com.example.taxi_project.dto.driver.DriverUpdate;
import com.example.taxi_project.enums.DriverStatus;
import com.example.taxi_project.repository.DriverRepository;
import com.example.taxi_project.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    public DriverResponse getById(UUID id) {
        return null;
    }

    @Override
    public DriverResponse update(UUID id, DriverUpdate updateDto) {
        return null;
    }

    @Override
    public void changeStatus(UUID id, DriverStatus status) {

    }

    @Override
    public BigDecimal getBalance(UUID id) {
        return null;
    }

    @Override
    public void updateCarInfo(UUID id, CarUpdate carUpdateDto) {

    }

    @Override
    public Double getRating(UUID id) {
        return 0.0;
    }
}
