package com.myfinwallet.finwallet_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    
}
