package com.myfinwallet.finwallet_api.dto.response;

import com.myfinwallet.finwallet_api.model.enums.TransactionStatus;
import com.myfinwallet.finwallet_api.model.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor


public class TransactionResponse {
    private UUID id;
    private BigDecimal amount;
    private String description;
    private TransactionStatus status;
    private TransactionType type;
    private String direction;
    private String counterpart;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
