package com.example.taxi_project.dto.order;

import com.example.taxi_project.enums.OrderStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyOrdersResponse {
    private UUID id;
    private String fromAddress;
    private String toAddress;
    private Double price;
    private OrderStatus status;
    private LocalDateTime createdAt;
}