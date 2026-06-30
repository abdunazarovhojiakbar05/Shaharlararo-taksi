package com.example.taxi_project.service;

import com.example.taxi_project.dto.driver.DriverTripResponse;
import com.example.taxi_project.dto.order.MyOrdersResponse;
import com.example.taxi_project.dto.order.OrderRequest;
import com.example.taxi_project.enums.OrderStatus;
import com.example.taxi_project.model.Order;
import com.example.taxi_project.model.User;
import com.example.taxi_project.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    Order createOrder(User user, OrderRequest request);

    List<MyOrdersResponse> getMyOrders(CustomUserDetails userDetails);

    DriverTripResponse acceptOrder(UUID id, String phone);

    Order startOrder(UUID id, CustomUserDetails userDetails);

    Order finishOrder(UUID id, CustomUserDetails userDetails);

    List<Order> findByStatus(OrderStatus orderStatus);

    List<Order> getDriverCurrentRoute(CustomUserDetails userDetails, double currentLat, double currentLon);
}