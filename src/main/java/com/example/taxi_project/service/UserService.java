package com.example.taxi_project.service;

import com.example.taxi_project.dto.order.MyOrdersResponse;
import com.example.taxi_project.dto.user.DriverApplyRequest;
import com.example.taxi_project.dto.user.UserResponse;
import com.example.taxi_project.dto.user.UserUpdate;
import com.example.taxi_project.security.CustomUserDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse getById(UUID id);

    UserResponse update(UUID id, UserUpdate updateDto);

    void delete(UUID id);


    List<UserResponse> getAll();
    void setActive(UUID id, boolean active);

 }
