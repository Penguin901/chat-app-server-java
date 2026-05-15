package com.example.chatapp.auth.domain;

import java.time.LocalDateTime;

public record IssuedTokens(
        String accessToken,
        LocalDateTime accessTokenExpiresAt,
        String refreshToken,
        LocalDateTime refreshTokenExpiresAt
) {
}