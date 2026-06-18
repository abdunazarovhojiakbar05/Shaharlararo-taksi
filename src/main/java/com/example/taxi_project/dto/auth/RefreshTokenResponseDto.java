package com.example.taxi_project.dto.auth;

import jakarta.persistence.JoinColumn;
import lombok.*;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponseDto {
    @JoinColumn(name = "refresh_token")
    private String refresh_token;

    @JoinColumn(name = "access_token")
    private String access_token;

    private Long expires_in = 900L;
}
