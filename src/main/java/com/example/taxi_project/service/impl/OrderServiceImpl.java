package com.example.taxi_project.service.impl;

import com.example.taxi_project.enums.OrderStatus;
import com.example.taxi_project.model.Driver;
import com.example.taxi_project.model.Order;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.OrderRepository;
import com.example.taxi_project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl  implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(User user, String from, String to) {
        Order order = new Order();
        order.setUser(user);
        order.setFromLocation(from);
        order.setToLocation(to);
        order.setStatus(OrderStatus.pending);
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
     public Order acceptOrder(UUID orderId, Driver driver) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Buyurtma topilmadi"));

        if (order.getStatus() != OrderStatus.pending) {
            throw new RuntimeException("Buyurtma allaqachon qabul qilingan!");
        }

        order.setDriver(driver);
        order.setStatus(OrderStatus.accepted);
        order.setAcceptedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public List<Order> findByStatus(OrderStatus orderStatus) {
        return orderRepository.findByStatus(orderStatus);
    }

    @Override
     public Order startOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.in_progress);
        order.setStartedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
     public Order finishOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.finished);
        order.setFinishedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}