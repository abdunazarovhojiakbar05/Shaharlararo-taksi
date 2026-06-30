package com.example.taxi_project.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "cars")
public class Cars {

    @Id
            @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    Driver driver_id;

    String model;

    int place;

    Long price;

    String picture;

 }
