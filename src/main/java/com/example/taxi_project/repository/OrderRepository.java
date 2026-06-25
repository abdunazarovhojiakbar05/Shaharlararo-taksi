package com.example.taxi_project.repository;

import com.example.taxi_project.enums.OrderStatus;
import com.example.taxi_project.model.Driver;
import com.example.taxi_project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStatus(OrderStatus orderStatus);

     @Query("SELECT o FROM Order o WHERE o.GroupId = :groupId")
    List<Order> findByGroupId(@Param("groupId") String groupId);

    List<Order> findByDriverAndStatus(Driver driver, OrderStatus orderStatus);
}
