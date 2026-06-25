package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.order.OrderRequest;
import com.example.taxi_project.enums.OrderStatus;
import com.example.taxi_project.exceptions.ValidationException;
import com.example.taxi_project.model.Driver;
import com.example.taxi_project.model.Order;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.OrderRepository;
import com.example.taxi_project.security.CustomUserDetails;
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
    public Order createOrder(User user, OrderRequest request) {
        Order order = new Order();
        order.setUser(user);
        order.setFromLat(request.getFromLat());
        order.setFromLon(request.getFromLon());
        order.setToLat(request.getToLat());
        order.setToLon(request.getToLon());
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
     public Order startOrder(UUID orderId, CustomUserDetails userDetails) {

        Driver driver = userDetails.getDriver();


        Order order = orderRepository.findById(orderId).orElseThrow();

        if(!order.getDriver().getId().equals(driver.getId())) throw new ValidationException("Buyurtma topilmadi");

        order.setStatus(OrderStatus.in_progress);

        order.setStartedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
     public Order finishOrder(UUID orderId, CustomUserDetails userDetails) {

        Driver driver = userDetails.getDriver();

        Order order = orderRepository.findById(orderId).orElseThrow();

        if(!order.getDriver().getId().equals(driver.getId())) throw new ValidationException("Buyurtma topilmadi");

        order.setStatus(OrderStatus.finished);
        order.setFinishedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}