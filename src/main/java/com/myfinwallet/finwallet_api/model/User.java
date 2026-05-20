package com.myfinwallet.finwallet_api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean locked = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

}


