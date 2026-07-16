package com.example.taxi_project.service.impl;

import com.example.taxi_project.enums.OrderStatus;
import com.example.taxi_project.exceptions.ValidationException;
import com.example.taxi_project.model.Driver;
import com.example.taxi_project.model.Order;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.DriverRepository;
import com.example.taxi_project.repository.OrderRepository;
import com.example.taxi_project.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Driver driver;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder().id(UUID.randomUUID()).build();
        driver = Driver.builder().id(UUID.randomUUID()).build();

        order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .status(OrderStatus.pending)
                .build();

        lenient().when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));    }

    @Test
    void user_cancelsOwnPendingOrder_success() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        CustomUserDetails userDetails = new CustomUserDetails(user, null);

        Order result = orderService.cancelOrder(order.getId(), userDetails, "Fikrimni o'zgartirdim");

        assertEquals(OrderStatus.cancelled, result.getStatus());
        assertEquals("Fikrimni o'zgartirdim", result.getCancelReason());
        assertNotNull(result.getCancelledAt());
    }

    @Test
    void user_cancelsSomeoneElsesOrder_throws() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        User anotherUser = User.builder().id(UUID.randomUUID()).build();
        CustomUserDetails userDetails = new CustomUserDetails(anotherUser, null);

        assertThrows(ValidationException.class,
                () -> orderService.cancelOrder(order.getId(), userDetails, null));
    }

    @Test
    void user_cancelsFinishedOrder_throws() {
        order.setStatus(OrderStatus.finished);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        CustomUserDetails userDetails = new CustomUserDetails(user, null);

        assertThrows(ValidationException.class,
                () -> orderService.cancelOrder(order.getId(), userDetails, null));
    }

    @Test
    void driver_cancelsAcceptedOrder_success() {
        order.setDriver(driver);
        order.setStatus(OrderStatus.accepted);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        CustomUserDetails driverDetails = new CustomUserDetails(driver);

        Order result = orderService.cancelOrder(order.getId(), driverDetails, "Mashina buzildi");

        assertEquals(OrderStatus.cancelled, result.getStatus());
        assertEquals("Mashina buzildi", result.getCancelReason());
    }

    @Test
    void driver_cancelsInProgressOrder_throws() {
        order.setDriver(driver);
        order.setStatus(OrderStatus.in_progress);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        CustomUserDetails driverDetails = new CustomUserDetails(driver);

        assertThrows(ValidationException.class,
                () -> orderService.cancelOrder(order.getId(), driverDetails, null));
    }

    @Test
    void driver_cancelsOrderNotAssignedToThem_throws() {
        Driver anotherDriver = Driver.builder().id(UUID.randomUUID()).build();
        order.setDriver(anotherDriver);
        order.setStatus(OrderStatus.accepted);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        CustomUserDetails driverDetails = new CustomUserDetails(driver);

        assertThrows(ValidationException.class,
                () -> orderService.cancelOrder(order.getId(), driverDetails, null));
    }

    @Test
    void cancelOrder_orderNotFound_throws() {
        UUID randomId = UUID.randomUUID();
        when(orderRepository.findById(randomId)).thenReturn(Optional.empty());
        CustomUserDetails userDetails = new CustomUserDetails(user, null);

        assertThrows(ValidationException.class,
                () -> orderService.cancelOrder(randomId, userDetails, null));
    }
}