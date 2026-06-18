package com.example.taxi_project.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "cars")
public class Cars {

    @Id
    UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    Driver driver_id;

    String model;

    int place;

    Long price;

    double summa;
}
