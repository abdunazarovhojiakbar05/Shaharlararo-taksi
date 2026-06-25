package com.example.taxi_project.dto.driver;


import com.example.taxi_project.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class DriverTripResponse {
    private String groupId;
    private List<Order> pickupRoute;
    private List<Order> deliveryRoute;
}