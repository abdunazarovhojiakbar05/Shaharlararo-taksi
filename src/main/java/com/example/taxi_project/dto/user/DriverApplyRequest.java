package com.example.taxi_project.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverApplyRequest {

    @NotBlank
    private String passportNumber;
    @NotBlank
    private String licenseNumber;

    @NotBlank
    private String carModel;
    @NotBlank
    private String carColor;
    @NotBlank
    private String plateNumber;
    @Min(2000)
    private int carYear;

    @NotNull
    private MultipartFile passportImage;
    @NotNull
    private MultipartFile licenseImage;
    @NotNull
    private MultipartFile carImage;
}