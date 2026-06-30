package com.example.taxi_project.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserUpdate {
    private String name;
    private String username;
}