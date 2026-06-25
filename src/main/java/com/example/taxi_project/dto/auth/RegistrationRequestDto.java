package com.example.taxi_project.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequestDto {
    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{9,13}$")
    String phone;
}