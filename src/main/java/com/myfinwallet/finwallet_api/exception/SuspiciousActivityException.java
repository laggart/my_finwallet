package com.myfinwallet.finwallet_api.exception;

public class SuspiciousActivityException extends RuntimeException {
    public SuspiciousActivityException(String reason) {
        super("Actividad sospechosa detectada: " + reason);
    }
}
