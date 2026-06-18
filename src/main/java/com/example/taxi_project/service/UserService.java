package com.example.taxi_project.service;

import com.example.taxi_project.dto.user.DriverApplyRequest;
import com.example.taxi_project.dto.user.UserResponse;
import com.example.taxi_project.dto.user.UserUpdate;

import java.math.BigDecimal;
import java.util.UUID;

public interface UserService {
    UserResponse getById(UUID id);

    UserResponse update(UUID id, UserUpdate updateDto);

    BigDecimal getBalance(UUID id);

    void topUpBalance(UUID id, BigDecimal amount);

    void delete(UUID id);

    void applyDriver(DriverApplyRequest request);
}
