package com.myfinwallet.finwallet_api.service;

import com.myfinwallet.finwallet_api.dto.request.TransferRequest;
import com.myfinwallet.finwallet_api.dto.response.TransactionResponse;
import com.myfinwallet.finwallet_api.exception.DailyLimitExceededException;
import com.myfinwallet.finwallet_api.exception.InsufficientFundsException;
import com.myfinwallet.finwallet_api.exception.ResourceNotFoundException;

import com.myfinwallet.finwallet_api.model.Account;
import com.myfinwallet.finwallet_api.model.Transaction;
import com.myfinwallet.finwallet_api.model.enums.TransactionStatus;
import com.myfinwallet.finwallet_api.model.enums.TransactionType;
import com.myfinwallet.finwallet_api.repository.AccountRepository;
import com.myfinwallet.finwallet_api.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor

public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final FraudDetectionService fraudDetectionService;

    @Transactional
    public TransactionResponse transfer(String senderEmail, TransferRequest request) {

        Account sender = accountRepository
                .findByUserIdWithLock(getSenderUserId(senderEmail))
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta origen no encontrada"));

        Account receiver = accountRepository
                .findByAccountNumberWithLock(request.getReceiverAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta destino no encontrada"));

        BigDecimal amount = request.getAmount();

        validateTransfer(sender, amount);
        fraudDetectionService.checkForSuspiciousActivity(sender, amount);

        sender.setBalance(sender.getBalance().subtract(amount));
        updateDailyLimit(sender, amount);
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction transaction = Transaction.builder()
                .senderAccount(sender)
                .receiverAccount(receiver)
                .amount(amount)
                .description(request.getDescription())
                .status(TransactionStatus.COMPLETED)
                .type(TransactionType.TRANSFER)
                .completedAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        transactionRepository.flush();
        transaction = transactionRepository.findById(transaction.getId())
             .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));

        return toResponse(transaction, sender.getId());
    }

    public Page<TransactionResponse> getHistory(String email, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Account account = accountService.findAccountByEmail(email);
        return transactionRepository
                .findByAccountIdAndDateRange(account.getId(), from, to, pageable)
                .map(t -> toResponse(t, account.getId()));
    }

    private UUID getSenderUserId(String email) {
        return accountService.findAccountByEmail(email).getUser().getId();
    }

    private void validateTransfer(Account sender, BigDecimal amount) {
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(sender.getBalance());
        }
        resetDailyLimitIfNeeded(sender);
        if (sender.getDailyTransferredAmount().add(amount)
                .compareTo(sender.getDailyTransferLimit()) > 0) {
            throw new DailyLimitExceededException(sender.getDailyTransferLimit());
        }
    }

    private void resetDailyLimitIfNeeded(Account account) {
        if (account.getLastTransferDate() == null ||
                !LocalDate.now().equals(account.getLastTransferDate())) {
            account.setDailyTransferredAmount(BigDecimal.ZERO);
            account.setLastTransferDate(LocalDate.now());
        }
    }

    private void updateDailyLimit(Account account, BigDecimal amount) {
        account.setDailyTransferredAmount(
                account.getDailyTransferredAmount().add(amount)
        );
        account.setLastTransferDate(LocalDate.now());
    }

    private TransactionResponse toResponse(Transaction t, UUID viewerAccountId) {
        boolean isOutgoing = t.getSenderAccount().getId().equals(viewerAccountId);
        String counterpart = isOutgoing
                ? t.getReceiverAccount().getUser().getFirstName() + " " + t.getReceiverAccount().getUser().getLastName()
                : t.getSenderAccount().getUser().getFirstName() + " " + t.getSenderAccount().getUser().getLastName();

        return TransactionResponse.builder()
                .id(t.getId())
                .amount(t.getAmount())
                .description(t.getDescription())
                .status(t.getStatus())
                .type(t.getType())
                .direction(isOutgoing ? "OUTGOING" : "INCOMING")
                .counterpart(counterpart)
                .createdAt(t.getCreatedAt())
                .completedAt(t.getCompletedAt())
                .build();
    }
}
