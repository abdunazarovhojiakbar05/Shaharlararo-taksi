package com.example.taxi_project.service.impl;

import com.example.taxi_project.model.Driver;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.DriverRepository;
import com.example.taxi_project.repository.UserRepository;
import com.example.taxi_project.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository usersRepository;
    private final DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = usersRepository.findByPhone(username);
        Optional<Driver> driver = driverRepository.findDriverByPhone(username);

        if (user.isPresent() || driver.isPresent()) {
            return new CustomUserDetails(user.orElse(null), driver.orElse(null));
        }

        throw new UsernameNotFoundException("Foydalanuvchi topilmadi: " + username);
    }
}