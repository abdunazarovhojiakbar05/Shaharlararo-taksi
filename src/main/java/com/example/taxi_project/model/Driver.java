package com.example.taxi_project.model;


import com.example.taxi_project.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity( name = "drivers")
@PrimaryKeyJoinColumn(name = "users_id")
public class Driver extends User{

    double rating;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    Cars car;


    DriverStatus status;

}
