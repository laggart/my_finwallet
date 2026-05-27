package com.myfinwallet.finwallet_api.service;

import com.myfinwallet.finwallet_api.dto.request.LoginRequest;
import com.myfinwallet.finwallet_api.dto.request.RegisterRequest;
import com.myfinwallet.finwallet_api.dto.response.AuthResponse;
import com.myfinwallet.finwallet_api.model.Account;
import com.myfinwallet.finwallet_api.model.User;
import com.myfinwallet.finwallet_api.repository.AccountRepository;
import com.myfinwallet.finwallet_api.repository.UserRepository;
import com.myfinwallet.finwallet_api.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .enabled(true) // por ahora sin verificación de email
            .locked(false)
            .build();
            
        user = userRepository.save(user);

        Account account = Account.builder()
            .accountNumber(generateAccountNumber())
            .balance(BigDecimal.ZERO)
            .dailyTransferLimit(new BigDecimal("1000.00"))
            .dailyTransferredAmount(BigDecimal.ZERO)
            .user(user)
            .build();


       account = accountRepository.save(account);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accesToken = jwtService.generateAccessToken(userDetails);

        return AuthResponse.builder()
            .accessToken(accesToken)
            .refreshToken(UUID.randomUUID().toString())
            .expiresIn(900000)
            .build(); 

    }

    public AuthResponse login (LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String accesToken = jwtService.generateAccessToken(userDetails);

        return AuthResponse.builder()
            .accessToken(accesToken)
            .refreshToken(UUID.randomUUID().toString())
            .expiresIn(900000)
            .build();        
    }

    private String generateAccountNumber() {
        return "FW-" + String.format("%010d", (long) (Math.random() * 9_999_999_999L));
    }
}
