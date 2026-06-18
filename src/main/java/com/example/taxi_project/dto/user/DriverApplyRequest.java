package com.example.taxi_project.dto.user;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverApplyRequest {

    private String passportNumber;
    private String licenseNumber;

    private String carModel;
    private String carColor;
    private String plateNumber;
    private int carYear;

    private MultipartFile passportImage;
    private MultipartFile licenseImage;
    private MultipartFile carImage;
}