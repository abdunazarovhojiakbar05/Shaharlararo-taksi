package com.example.taxi_project.dto.order;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    String from;
    String to;
}
