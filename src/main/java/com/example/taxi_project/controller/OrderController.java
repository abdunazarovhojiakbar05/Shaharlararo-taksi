

package com.example.taxi_project.controller;

import com.example.taxi_project.dto.driver.DriverTripResponse;
import com.example.taxi_project.dto.order.MyOrdersResponse;
import com.example.taxi_project.dto.order.OrderRequest;
import com.example.taxi_project.enums.OrderStatus;
import com.example.taxi_project.model.Order;
import com.example.taxi_project.security.CustomUserDetails;
import com.example.taxi_project.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Mijoz buyurtma beradi")
    public ResponseEntity<Order> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(userDetails.getUser(), request));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Driver bo'sh buyurtmalarni ko'radi")
    public ResponseEntity<List<Order>> getPending() {
        return ResponseEntity.ok(orderService.findByStatus(OrderStatus.pending));
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "Driver qabul qiladi")
    public ResponseEntity<DriverTripResponse> accept(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String driverPhone = userDetails.getUser().getPhone();

        return ResponseEntity.ok(orderService.acceptOrder(id, driverPhone));
    }

    @PostMapping("/{id}/rate")
    @Operation(summary = "Safarni baholash (1-5 yulduz)")
    public ResponseEntity<Order> rate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @RequestParam int rating) {
        return ResponseEntity.ok(orderService.rateOrder(id, userDetails, rating));
    }


    @GetMapping("/my")
    public ResponseEntity<List<MyOrdersResponse>> getMyOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(orderService.getMyOrders(userDetails));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Safarni boshlash")
    public ResponseEntity<Order> start(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.startOrder(id, userDetails));
    }

    @PostMapping("/{id}/finish")
    @Operation(summary = "Safarni tugatish")
    public ResponseEntity<Order> finish(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.finishOrder(id, userDetails));
    }

    @GetMapping("/current-route")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Haydovchining joriy joylashuviga qarab marshrutini qayta saralab berish")
    public ResponseEntity<List<Order>> getCurrentRoute(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam double currentLat,
            @RequestParam double currentLon) {
        return ResponseEntity.ok(orderService.getDriverCurrentRoute(userDetails, currentLat, currentLon));
    }


    @PostMapping("/{id}/cancel")
    @Operation(summary = "Buyurtmani bekor qilish (mijoz yoki haydovchi tomonidan)")
    public ResponseEntity<Order> cancel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(orderService.cancelOrder(id, userDetails, reason));
    }
}


