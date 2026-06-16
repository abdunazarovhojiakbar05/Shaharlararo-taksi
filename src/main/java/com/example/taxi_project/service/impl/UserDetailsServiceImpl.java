package com.example.taxi_project.service.impl;

import com.example.taxi_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements  org.springframework.security.core.userdetails.UserDetailsService{

    private final UserRepository usersRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        throw new UsernameNotFoundException("Foydalanuvchi topilmadi: " + username);
    }
}