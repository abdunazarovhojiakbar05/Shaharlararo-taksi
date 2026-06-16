package com.example.taxi_project.repository;

import com.example.taxi_project.enums.OrderStatus;
import com.example.taxi_project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStatus(OrderStatus orderStatus);
}
