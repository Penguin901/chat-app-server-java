package com.example.chatapp.security.jwt;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * WebSocket(STOMP) 메시지 헤더에서 Bearer 토큰을 추출합니다.
 */
@Component
public class StompBearerTokenResolver {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    public String resolve(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);

        if (header == null || header.isBlank()) return null;

        if (!header.startsWith(TOKEN_PREFIX)) return null;

        return header.substring(TOKEN_PREFIX.length());
    }
}
