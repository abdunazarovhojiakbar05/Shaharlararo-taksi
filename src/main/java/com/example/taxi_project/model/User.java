package com.example.taxi_project.model;




import com.example.taxi_project.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "users_type")
@Entity( name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long chat_id;

    private String username;

    private String name;


    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    private String code;

    private LocalDateTime expired_at;



}
