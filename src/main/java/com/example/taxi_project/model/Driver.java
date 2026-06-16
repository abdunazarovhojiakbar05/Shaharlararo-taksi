package com.example.taxi_project.model;


import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
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

}
