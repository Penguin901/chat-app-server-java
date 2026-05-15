package com.example.chatapp.auth.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {
}
