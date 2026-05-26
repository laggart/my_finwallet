package com.myfinwallet.finwallet_api.exception;

import java.math.BigDecimal;

public class DailyLimitExceededException extends RuntimeException {
    public DailyLimitExceededException(BigDecimal limit) {
        super("Has alcanzado el límite diario de transferencias (" + limit + "€)");
    }
    
}
