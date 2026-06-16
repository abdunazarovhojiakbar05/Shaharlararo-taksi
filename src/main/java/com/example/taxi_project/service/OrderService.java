package com.example.taxi_project.service;

import com.example.taxi_project.enums.OrderStatus;
import com.example.taxi_project.model.Driver;
import com.example.taxi_project.model.Order;
import com.example.taxi_project.model.User;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order startOrder(UUID id);

    Order finishOrder(UUID id);

    Order createOrder(User user, String from, String to);

    Order acceptOrder(UUID id, Driver driver);

    List<Order> findByStatus(OrderStatus orderStatus);
}
