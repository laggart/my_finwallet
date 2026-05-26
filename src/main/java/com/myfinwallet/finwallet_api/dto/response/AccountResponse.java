package com.myfinwallet.finwallet_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor

public class AccountResponse {
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal dailyTransferLimit;
    private BigDecimal dailyTransferredAmount;
    private String ownerName;
    
}
