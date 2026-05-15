package com.example.chatapp.auth;

import com.example.chatapp.auth.domain.IssuedTokens;
import com.example.chatapp.auth.domain.RefreshTokenInfo;
import com.example.chatapp.auth.dto.RefreshTokenResponse;
import com.example.chatapp.common.exception.AuthException;
import com.example.chatapp.common.exception.ErrorCode;
import com.example.chatapp.security.jwt.JwtService;
import com.example.chatapp.user.User;
import com.example.chatapp.user.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    public IssuedTokens issueTokens(Long userId) {
        IssuedTokens newTokens = jwtService.createTokens(userId);
        RefreshTokenInfo refreshTokenInfo = new RefreshTokenInfo(newTokens.refreshToken(), newTokens.refreshTokenExpiresAt());

        userService.updateRefreshToken(userId, refreshTokenInfo);

        return newTokens;
    }

    @Override
    @Transactional
    public RefreshTokenResponse reissueToken(String refreshToken) {
        Claims claims = jwtService.validateRefreshToken(refreshToken);

        Long userId = Long.parseLong(claims.getSubject());
        User user = userService.getUserOrThrow(userId);

        if (user.getRefreshToken() == null || !refreshToken.equals(user.getRefreshToken())) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        LocalDateTime expiration = user.getRefreshExpiration();

        // 저장된 만료시간이 null인 경우 유효하지 않은 토큰으로 처리
        if (expiration == null) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 현재시간이 만료시간과 같거나 이전인 경우 만료된 토큰으로 처리
        if (!LocalDateTime.now().isBefore(expiration)) {
            throw new AuthException(ErrorCode.EXPIRED_TOKEN);
        }

        IssuedTokens newTokens = issueTokens(userId);

        return new RefreshTokenResponse(
                newTokens.accessToken(),
                newTokens.refreshToken()
        );
    }
}

