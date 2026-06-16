package com.example.taxi_project.dto.user;

import com.example.taxi_project.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private UUID id;

    private String username;

    private String name;

    private String phone;

    private UserRole role;

    private boolean is_active;

    private int code;

    private LocalDateTime expired_at;

    private boolean isIs_active;
}
