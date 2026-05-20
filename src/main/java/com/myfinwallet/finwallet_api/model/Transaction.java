package com.myfinwallet.finwallet_api.model;

import com.myfinwallet.finwallet_api.model.enums.TransactionStatus;
import com.myfinwallet.finwallet_api.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_account_id", nullable = false)
    private Account senderAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_account_id", nullable = false)
    private Account receiverAccount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    private String failureReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
    
}
