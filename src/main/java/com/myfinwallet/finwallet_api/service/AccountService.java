package com.myfinwallet.finwallet_api.service;

import com.myfinwallet.finwallet_api.dto.response.AccountResponse;
import com.myfinwallet.finwallet_api.exception.ResourceNotFoundException;
import com.myfinwallet.finwallet_api.model.Account;
import com.myfinwallet.finwallet_api.repository.AccountRepository;
import com.myfinwallet.finwallet_api.repository.UserRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountResponse getMyAccount(String email) {
        Account account = findAccountByEmail(email);
        return toResponse(account);
    }

    public Account findAccountByEmail(String email) {
        return userRepository.findByEmail(email)
                .flatMap(user -> accountRepository.findByUserId(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
    }

    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .dailyTransferLimit(account.getDailyTransferLimit())
                .dailyTransferredAmount(account.getDailyTransferredAmount())
                .ownerName(account.getUser().getFirstName() + " " + account.getUser().getLastName())
                .build();
    }

}
