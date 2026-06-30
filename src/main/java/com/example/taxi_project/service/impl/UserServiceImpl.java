package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.order.MyOrdersResponse;
import com.example.taxi_project.dto.user.DriverApplyRequest;
import com.example.taxi_project.dto.user.UserResponse;
import com.example.taxi_project.dto.user.UserUpdate;
import com.example.taxi_project.exceptions.ResourceNotFoundException;
import com.example.taxi_project.model.Order;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.OrderRepository;
import com.example.taxi_project.repository.UserRepository;
import com.example.taxi_project.security.CustomUserDetails;
import com.example.taxi_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public UserResponse getById(UUID id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi: " + id));

        return toResponse(user);
    }

    @Override
    public UserResponse update(UUID id, UserUpdate updateDto) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi: " + id));
        user.setUsername(updateDto.getUsername() == null ? user.getUsername() : updateDto.getUsername());
        user.setName(updateDto.getName() == null ? user.getName() : updateDto.getName());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }



    @Override
    public void delete(UUID id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi: " + id));

        user.setActive(false);
        userRepository.save(user);

    }




    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .code(user.getCode())
                .expired_at(user.getExpired_at())
                .isIs_active(user.isActive())
                .build();
    }
}