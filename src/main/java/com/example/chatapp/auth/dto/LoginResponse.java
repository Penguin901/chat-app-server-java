package com.example.chatapp.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        AccountInfo accountInfo
) {
}

// LocalDateTime accessTokenExpiresAt,
// LocalDateTime refreshTokenExpiresAt,
