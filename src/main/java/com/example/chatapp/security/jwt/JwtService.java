package com.example.chatapp.security.jwt;

import com.example.chatapp.common.exception.AuthException;
import com.example.chatapp.common.exception.ErrorCode;
import com.example.chatapp.auth.domain.IssuedTokens;
import com.example.chatapp.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static io.jsonwebtoken.Jwts.parser;

/**
 * 로그인 후 사용자 인증 토큰(JSON Web Token)을 생성하고, 클라이언트의 요청에 포함된 토큰의 유효성을 검증합니다.
 */
@Component
public class JwtService {

    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";
    private final SecretKey secretKey;

    public JwtService(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public IssuedTokens createTokens(Long userId) {
        if (secretKey == null) {
            throw new IllegalStateException("Secret key is null");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenExpirationAt = now.plusHours(1);
        LocalDateTime refreshTokenExpirationAt = now.plusDays(5);

        Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationAt.atZone(ZoneId.systemDefault()).toInstant());
        Date refreshTokenExpirationDate = Date.from(refreshTokenExpirationAt.atZone(ZoneId.systemDefault()).toInstant());

        String accessToken = Jwts.builder()
                .claim("sub", String.valueOf(userId))
                .claim("role", List.of("USER_EDIT"))
                .claim("type", ACCESS_TOKEN_TYPE)
                .issuedAt(issuedAt)
                .expiration(accessTokenExpirationDate)
                .signWith(secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .claim("sub", String.valueOf(userId))
                .claim("role", List.of("USER_EDIT"))
                .claim("type", REFRESH_TOKEN_TYPE)
                .issuedAt(issuedAt)
                .expiration(refreshTokenExpirationDate)
                .signWith(secretKey)
                .compact();

        return new IssuedTokens(
                accessToken,
                accessTokenExpirationAt,
                refreshToken,
                refreshTokenExpirationAt);
    }

    public Claims validateAccessToken(String accessToken) {
        Claims claims = verifyToken(accessToken);

        if (!ACCESS_TOKEN_TYPE.equals(claims.get("type", String.class))) {
            throw new AuthException(ErrorCode.INVALID_TOKEN_TYPE);
        }

        return claims;
    }

    public Claims validateRefreshToken(String refreshToken) {
        Claims claims = verifyToken(refreshToken);

        if (!REFRESH_TOKEN_TYPE.equals(claims.get("type", String.class))) {
            throw new AuthException(ErrorCode.INVALID_TOKEN_TYPE);
        }

        return claims;
    }

    // 사용자 인증 객체 생성
    public Authentication getAuthentication(Claims claims) {
        Long userId = Long.valueOf(claims.getSubject());

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER_EDIT"));
        UserPrincipal userPrincipal = new UserPrincipal(userId, authorities);

        return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.authorities());
    }

    // 토큰 유효성 검사
    private Claims verifyToken(String token) {
        try {
            return parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token) // 토큰 파싱 후 claims 반환
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }
    }
}

