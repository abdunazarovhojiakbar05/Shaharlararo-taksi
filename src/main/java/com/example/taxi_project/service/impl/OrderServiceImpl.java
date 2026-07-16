package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.driver.DriverTripResponse;
import com.example.taxi_project.dto.order.MyOrdersResponse;
import com.example.taxi_project.dto.order.OrderRequest;
import com.example.taxi_project.enums.OrderStatus;
import com.example.taxi_project.exceptions.ValidationException;
import com.example.taxi_project.model.Driver;
import com.example.taxi_project.model.Order;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.OrderRepository;
import com.example.taxi_project.repository.DriverRepository;
import com.example.taxi_project.security.CustomUserDetails;
import com.example.taxi_project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private static final double MAX_PICKUP_RADIUS_METERS = 3000.0;

    @Transactional
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

        String tempGroupId = UUID.randomUUID().toString();
        order.setGroupId(tempGroupId);

        order = orderRepository.save(order);

        List<Order> activePendingOrders = orderRepository.findByStatus(OrderStatus.pending);
        List<Order> matchInGroup = new ArrayList<>();
        matchInGroup.add(order);

        for (Order existingOrder : activePendingOrders) {
            if (existingOrder.getId().equals(order.getId())) continue;

            double pickupDistance = calculateHaversineDistance(order.getFromLat(), order.getFromLon(), existingOrder.getFromLat(), existingOrder.getFromLon());
            double dropoffDistance = calculateHaversineDistance(order.getToLat(), order.getToLon(), existingOrder.getToLat(), existingOrder.getToLon());

            if (pickupDistance <= MAX_PICKUP_RADIUS_METERS && dropoffDistance <= 15000.0) {
                matchInGroup.add(existingOrder);
                if (matchInGroup.size() == 4) {
                    break;
                }
            }
        }

        if (matchInGroup.size() == 4) {
            String groupId = UUID.randomUUID().toString();
            for (Order match : matchInGroup) {
                match.setGroupId(groupId);
                orderRepository.save(match);
            }
        }

        return order;
    }

    @Override
    public List<MyOrdersResponse> getMyOrders(CustomUserDetails userDetails) {

        UUID id = userDetails.isParent() ? userDetails.getUser().getId() : userDetails.getDriver().getId();
        List<Order> list = userDetails.isParent()
                ? orderRepository.findByUserId(id)
                : orderRepository.findByDriverId(id);
        List<MyOrdersResponse> myOrdersResponseList = new ArrayList<>();

        for (Order order : list) {
            MyOrdersResponse dto = new MyOrdersResponse();
            dto.setId(order.getId());
            dto.setStatus(order.getStatus());
            dto.setPrice(order.getPrice());
            dto.setCreatedAt(order.getCreatedAt());

            String from = order.getFromLat() + ", " + order.getFromLon();
            String to = order.getToLat() + ", " + order.getToLon();

            dto.setFromAddress(from);
            dto.setToAddress(to);

            myOrdersResponseList.add(dto);
        }

        return myOrdersResponseList;
    }

    @Transactional
    @Override
    public DriverTripResponse acceptOrder(UUID orderId, String phone) {

        System.out.println(phone);
        String cleanedPhone = phone.trim();
        System.out.println("Cleaned Phone : " + cleanedPhone);

        Driver driver = driverRepository.findByPhone(cleanedPhone).orElseThrow(() -> new RuntimeException("Siz haydovchi sifatida ro'yxatdan o'tmagansiz!"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Buyurtma topilmadi"));

        if (order.getStatus() != OrderStatus.pending) {
            throw new RuntimeException("Buyurtma allaqachon qabul qilingan!");
        }

        String groupId = order.getGroupId();
        List<Order> optimizedPickup = new ArrayList<>();
        List<Order> optimizedDelivery = new ArrayList<>();

        if (groupId != null) {
            List<Order> groupOrders = orderRepository.findByGroupId(groupId);
            for (Order gOrder : groupOrders) {
                gOrder.setDriver(driver);
                gOrder.setStatus(OrderStatus.accepted);
                gOrder.setAcceptedAt(LocalDateTime.now());
                orderRepository.save(gOrder);
            }

            double driverLat = driver.getCurrentLat();
            double driverLon = driver.getCurrentLon();

            optimizedPickup = getOptimizedPickupRoute(driverLat, driverLon, groupId);
            optimizedDelivery = getOptimizedDeliveryRoute(optimizedPickup);
        } else {
            order.setDriver(driver);
            order.setStatus(OrderStatus.accepted);
            order.setAcceptedAt(LocalDateTime.now());
            Order saved = orderRepository.save(order);
            optimizedPickup.add(saved);
            optimizedDelivery.add(saved);
            groupId = "single_" + saved.getId();
        }

        return new DriverTripResponse(groupId, optimizedPickup, optimizedDelivery);
    }

    @Override
    public List<Order> findByStatus(OrderStatus orderStatus) {
        return orderRepository.findByStatus(orderStatus);
    }

    @Transactional
    @Override
    public Order startOrder(UUID orderId, CustomUserDetails userDetails) {
        Driver driver = driverRepository.findByPhone(userDetails.getUser().getPhone())
                .orElseThrow(() -> new RuntimeException("Haydovchi topilmadi"));

        Order order = orderRepository.findById(orderId).orElseThrow();

        if (!order.getDriver().getId().equals(driver.getId())) {
            throw new ValidationException("Buyurtma topilmadi");
        }

        order.setStatus(OrderStatus.in_progress);
        order.setStartedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Transactional
    @Override
    public Order finishOrder(UUID orderId, CustomUserDetails userDetails) {
        Driver driver = driverRepository.findByPhone(userDetails.getUser().getPhone())
                .orElseThrow(() -> new RuntimeException("Haydovchi topilmadi"));

        Order order = orderRepository.findById(orderId).orElseThrow();

        if (!order.getDriver().getId().equals(driver.getId())) {
            throw new ValidationException("Buyurtma topilmadi");
        }

        order.setStatus(OrderStatus.finished);
        order.setFinishedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getDriverCurrentRoute(CustomUserDetails userDetails, double currentLat, double currentLon) {
        Driver driver = driverRepository.findByPhone(userDetails.getUser().getPhone())
                .orElseThrow(() -> new RuntimeException("Haydovchi topilmadi"));

        List<Order> activeOrders = orderRepository.findByDriverAndStatus(driver, OrderStatus.accepted);

        if (activeOrders.isEmpty()) {
            return new ArrayList<>();
        }

        String groupId = activeOrders.get(0).getGroupId();
        return getOptimizedPickupRoute(currentLat, currentLon, groupId);
    }

    @Transactional
    @Override
    public Order cancelOrder(UUID orderId, CustomUserDetails userDetails, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException("Buyurtma topilmadi"));

        if (order.getStatus() == OrderStatus.finished) {
            throw new ValidationException("Bu buyurtma allaqachon tugagan, uni bekor qilib bo'lmaydi");
        }
        if (order.getStatus() == OrderStatus.cancelled) {
            throw new ValidationException("Bu buyurtma allaqachon bekor qilingan");
        }

        if (userDetails.isParent()) {
            if (!order.getUser().getId().equals(userDetails.getUser().getId())) {
                throw new ValidationException("Bu buyurtma sizga tegishli emas");
            }
        } else {
            if (order.getDriver() == null || !order.getDriver().getId().equals(userDetails.getDriver().getId())) {
                throw new ValidationException("Bu buyurtma sizga tegishli emas");
            }
            if (order.getStatus() != OrderStatus.accepted) {
                throw new ValidationException("Safar boshlangandan keyin buyurtmani bekor qilib bo'lmaydi");
            }
        }

        order.setStatus(OrderStatus.cancelled);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelReason(reason);

        return orderRepository.save(order);
    }

    @Transactional
    @Override
    public Order rateOrder(UUID orderId, CustomUserDetails userDetails, int rating) {
        if (rating < 0 || rating > 5) {
            throw new ValidationException("Baho 0 dan 5 gacha bo'lishi kerak");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException("Buyurtma topilmadi"));

        if (!order.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new ValidationException("Bu buyurtma sizga tegishli emas");
        }
        if (order.getStatus() != OrderStatus.finished) {
            throw new ValidationException("Faqat tugagan safarni baholash mumkin");
        }
        if (order.getRating() != null) {
            throw new ValidationException("Bu buyurtma allaqachon baholangan");
        }

        order.setRating(rating);
        orderRepository.save(order);

        Driver driver = order.getDriver();
        double newAvg = (driver.getRating() * driver.getRatingCount() + rating) / (double) (driver.getRatingCount() + 1);
        driver.setRating(newAvg);
        driver.setRatingCount(driver.getRatingCount() + 1);
        driverRepository.save(driver);

        return order;
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    public List<Order> getOptimizedPickupRoute(double driverLat, double driverLon, String groupId) {
        List<Order> remainingOrders = orderRepository.findByGroupId(groupId);
        List<Order> optimizedRoute = new ArrayList<>();

        double currentLat = driverLat;
        double currentLon = driverLon;

        while (!remainingOrders.isEmpty()) {
            Order closestOrder = null;
            double minDistance = Double.MAX_VALUE;
            int closestIndex = -1;

            for (int i = 0; i < remainingOrders.size(); i++) {
                Order order = remainingOrders.get(i);
                double distance = calculateHaversineDistance(currentLat, currentLon, order.getFromLat(), order.getFromLon());

                if (distance < minDistance) {
                    minDistance = distance;
                    closestOrder = order;
                    closestIndex = i;
                }
            }

            if (closestOrder != null) {
                optimizedRoute.add(closestOrder);
                remainingOrders.remove(closestIndex);
                currentLat = closestOrder.getFromLat();
                currentLon = closestOrder.getFromLon();
            }
        }
        return optimizedRoute;
    }

    public List<Order> getOptimizedDeliveryRoute(List<Order> groupOrders) {
        List<Order> sortedDelivery = new ArrayList<>(groupOrders);
        sortedDelivery.sort((o1, o2) -> {
            double startLat = groupOrders.get(0).getFromLat();
            double startLon = groupOrders.get(0).getFromLon();

            double dist1 = calculateHaversineDistance(startLat, startLon, o1.getToLat(), o1.getToLon());
            double dist2 = calculateHaversineDistance(startLat, startLon, o2.getToLat(), o2.getToLon());

            return Double.compare(dist1, dist2);
        });
        return sortedDelivery;
    }
}