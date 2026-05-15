package com.example.chatapp.auth.domain;

import com.example.chatapp.common.exception.AuthException;
import com.example.chatapp.common.exception.ErrorCode;

import java.time.LocalDateTime;

public record RefreshTokenInfo(
        String refreshToken,
        LocalDateTime refreshExpiration
) {
    public RefreshTokenInfo {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }

        if (!refreshExpiration.isAfter(LocalDateTime.now())) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }
    }
}



