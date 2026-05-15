package com.example.chatapp.security.jwt;

import com.example.chatapp.common.exception.AuthException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HttpBearerTokenResolver httpBearerTokenResolver;
    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 로그인, 토큰 재발급, 웹소켓 요청은 필터에서 제외
        String[] excludePath = {"/auth/login", "/auth/refresh", "/ws"};
        String path = request.getRequestURI();

        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = httpBearerTokenResolver.resolve(request);

        if (token != null) {
            try {
                Claims claims = jwtService.validateAccessToken(token);
                Authentication authentication = jwtService.getAuthentication(claims);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthException e) {
                // 인증 실패시 인증 정보 초기화
                SecurityContextHolder.clearContext();
                throw new AuthenticationCredentialsNotFoundException("Authentication failed", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}