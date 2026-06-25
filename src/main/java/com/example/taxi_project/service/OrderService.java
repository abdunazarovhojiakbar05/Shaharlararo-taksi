package com.example.taxi_project.service;

import com.example.taxi_project.dto.order.OrderRequest;
import com.example.taxi_project.enums.OrderStatus;
import com.example.taxi_project.model.Driver;
import com.example.taxi_project.model.Order;
import com.example.taxi_project.model.User;
import com.example.taxi_project.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order startOrder(UUID id, CustomUserDetails userDetails);

    Order finishOrder(UUID id, CustomUserDetails userDetails);

    Order createOrder(User user, OrderRequest request);

    Order acceptOrder(UUID id, Driver driver);

    List<Order> findByStatus(OrderStatus orderStatus);
}
