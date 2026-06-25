package com.example.taxi_project.model;

import com.example.taxi_project.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private UUID id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = true)
    private Driver driver;

    private Double fromLat;
    private Double fromLon;
    private Double toLat;
    private Double toLon;


    private Double price;

    private String GroupId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    private LocalDateTime createdAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

}