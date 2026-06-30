package com.example.taxi_project.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    @NotNull(message = "Jo'nash joyi kengligi (Lat) kiritilishi shart")
    private Double fromLat;

    @NotNull(message = "Jo'nash joyi uzoqligi (Lon) kiritilishi shart")
    private Double fromLon;

    @NotNull(message = "Boradigan joy kengligi (Lat) kiritilishi shart")
    private Double toLat;

    @NotNull(message = "Boradigan joy uzoqligi (Lon) kiritilishi shart")
    private Double toLon;


}