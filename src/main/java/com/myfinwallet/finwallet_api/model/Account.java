package com.myfinwallet.finwallet_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyTransferLimit;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyTransferredAmount;

    private LocalDate lastTransferDate;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Version
    private Long version;

}
