package com.myfinwallet.finwallet_api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.myfinwallet.finwallet_api.exception.SuspiciousActivityException;
import com.myfinwallet.finwallet_api.model.Account;
import com.myfinwallet.finwallet_api.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;

    public void checkForSuspiciousActivity(Account sender, BigDecimal amount) {
        checkTransactionFrequency(sender);
        checkLargeTransfer(sender, amount);
    }

    private void checkTransactionFrequency(Account sender) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        Long count = transactionRepository.countByAccountInLastHour(sender.getId(), oneHourAgo);
        if (count >= 5) {
            throw new SuspiciousActivityException(
                "Demasiadas transferencias en poco tiempo (" + count + " en la última hora)"
            );
        }

        }

    private void checkLargeTransfer(Account sender, BigDecimal amount) {
        if (sender.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = amount.divide(sender.getBalance(), 2, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            if (percentage.compareTo(new BigDecimal("80")) > 0) {
                throw new SuspiciousActivityException(
                    "Transferencia superior al 80% del saldo disponible"
                );
            }
        }
    }
    
}
