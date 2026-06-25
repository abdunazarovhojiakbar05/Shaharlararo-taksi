package com.example.taxi_project.controller;

import com.example.taxi_project.dto.order.OrderRequest;
import com.example.taxi_project.enums.OrderStatus;
import com.example.taxi_project.model.Order;
import com.example.taxi_project.security.CustomUserDetails;
import com.example.taxi_project.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    @Operation(summary = " Mijoz buyurtma beradi")
    public ResponseEntity<Order> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(userDetails.getUser(), request));
    }

    @GetMapping("/pending")
    @Operation(summary = " Driver bo'sh buyurtmalarni ko'radi")
    public ResponseEntity<List<Order>> getPending() {
        return ResponseEntity.ok(
                orderService.findByStatus(OrderStatus.pending)
        );
    }


    @PostMapping("/{id}/accept")
    @Operation(summary = " Driver qabul qiladi")
    public ResponseEntity<Order> accept(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                orderService.acceptOrder(id, userDetails.getDriver())
        );
    }

    @PostMapping("/{id}/start")
    @Operation(summary = " Safarni boshlash")
    public ResponseEntity<Order> start(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.startOrder(id, userDetails));
    }


    @PostMapping("/{id}/finish")
    @Operation(summary = " Safarni tugatish")
    public ResponseEntity<Order> finish(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.finishOrder(id, userDetails));
    }
}