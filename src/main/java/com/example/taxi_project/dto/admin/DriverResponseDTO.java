package com.example.taxi_project.dto.admin;

import com.example.taxi_project.enums.ApplicationStatus;
import lombok.*;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverResponseDTO {

    private String name;
    private String phone;
    private String username;
    private String passport_image;
    private String licence_image;
    private ApplicationStatus status_application;
}
