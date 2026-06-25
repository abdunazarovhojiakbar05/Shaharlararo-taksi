package com.example.taxi_project.model;


import com.example.taxi_project.enums.ApplicationStatus;
import com.example.taxi_project.enums.DriverStatus;
import com.example.taxi_project.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "drivers")
public class Driver {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long chat_id;

    private String username;

    private String name;


    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    private String code;

    private LocalDateTime expired_at;

    private ApplicationStatus status_application;


    double rating;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    Cars car;


    DriverStatus status;

     private Double currentLat;
     private Double currentLon;
}
