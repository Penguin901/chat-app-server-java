package com.example.chatapp.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

/**
 * HTTP 요청의 헤더에서 Bearer 토큰을 추출합니다.
 */
@Component
public class HttpBearerTokenResolver implements BearerTokenResolver {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public String resolve(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);

        if (header == null || header.isBlank()) return null;

        if (!header.startsWith(TOKEN_PREFIX)) return null;

        return header.substring(TOKEN_PREFIX.length());
    }
}