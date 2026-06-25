package com.example.taxi_project.repository;

import com.example.taxi_project.enums.ApplicationStatus;
import com.example.taxi_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserById(UUID id);

    Optional<User> findByUsername(String username);

     @Query("SELECT u FROM users  u WHERE u.chat_id = :chatId")
    Optional<User> findByChatId(@Param("chatId") Long chatId);

    Optional<User> findByCode(String code);

    Optional<Object> findByPhoneAndCode(String phone, String code);

    @Query("SELECT u FROM users u WHERE u.status_application = :status")
    List<User> findAllByStatusApplication(@Param("status") ApplicationStatus status);}