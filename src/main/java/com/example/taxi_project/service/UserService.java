package com.example.taxi_project.service;

import com.example.taxi_project.dto.user.DriverApplyRequest;
import com.example.taxi_project.dto.user.UserResponse;
import com.example.taxi_project.dto.user.UserUpdate;

import java.math.BigDecimal;
import java.util.UUID;

public interface UserService {
    UserResponse getById(UUID id);

    UserResponse update(UUID id, UserUpdate updateDto);


    void delete(UUID id);

 }
