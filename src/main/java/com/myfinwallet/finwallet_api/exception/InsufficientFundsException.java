package com.myfinwallet.finwallet_api.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(BigDecimal availableBalance) {
        super("Saldo insuficiente. Disponible: " + availableBalance + "€");
    }
}
