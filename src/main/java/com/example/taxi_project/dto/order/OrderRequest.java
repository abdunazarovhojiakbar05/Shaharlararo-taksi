package com.example.taxi_project.dto.order;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NotBlank
 public class OrderRequest {

    private Double fromLat;
    private Double fromLon;
    private Double toLat;
    private Double toLon;
}