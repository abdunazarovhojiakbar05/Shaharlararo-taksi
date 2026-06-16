package com.example.taxi_project.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "error_logs")
public class ErrorLog {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID user_id;

    @Column(columnDefinition = "TEXT")
    private String error_message;

    private String path;

    private int status;

    private String exception_type;

    private LocalDateTime created_at;

}
