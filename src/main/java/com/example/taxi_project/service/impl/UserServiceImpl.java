package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.user.UserResponse;
import com.example.taxi_project.dto.user.UserUpdate;
import com.example.taxi_project.exceptions.ResourceNotFoundException;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.UserRepository;
import com.example.taxi_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getById(UUID id) {

        User user = userRepository.findUserById(id).orElseThrow( () -> new ResourceNotFoundException("Foydalanuvchi topilmadi")  );
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .code(user.getCode())
                .expired_at(user.getExpired_at())
                .isIs_active(true)
                .build();

        return response;
    }

    @Override
    public UserResponse update(UUID id, UserUpdate updateDto) {
        return null;
    }

    @Override
    public BigDecimal getBalance(UUID id) {
        return null;
    }

    @Override
    public void topUpBalance(UUID id, BigDecimal amount) {

    }

    @Override
    public void delete(UUID id) {

    }
}
