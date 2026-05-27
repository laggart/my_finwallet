package com.myfinwallet.finwallet_api.controller;

import com.myfinwallet.finwallet_api.dto.request.LoginRequest;
import com.myfinwallet.finwallet_api.dto.request.RegisterRequest;
import com.myfinwallet.finwallet_api.dto.response.AuthResponse;
import com.myfinwallet.finwallet_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

     private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
}
