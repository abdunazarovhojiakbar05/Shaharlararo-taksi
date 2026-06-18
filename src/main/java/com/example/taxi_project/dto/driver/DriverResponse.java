package com.example.taxi_project.dto.driver;

import com.example.taxi_project.enums.UserRole;
import lombok.*;
import org.springframework.web.service.annotation.GetExchange;

import java.util.UUID;


 @Getter
 @Setter
 @AllArgsConstructor
 @NoArgsConstructor
 @Builder
public class DriverResponse {
    double rating;

    private UUID id;

    private String username;

    private String name;

    private String phone;

    private UserRole role;

    private boolean is_active;


}
