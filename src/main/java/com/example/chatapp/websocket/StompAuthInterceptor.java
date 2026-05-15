package com.example.chatapp.websocket;

import com.example.chatapp.common.exception.AuthException;
import com.example.chatapp.security.jwt.JwtService;
import com.example.chatapp.security.jwt.StompBearerTokenResolver;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final StompBearerTokenResolver stompBearerTokenResolver;
    private final JwtService jwtService;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = stompBearerTokenResolver.resolve(accessor);

            if (token == null) {
                throw new AccessDeniedException("Token is null");
            }

            try {
                Claims claims = jwtService.validateAccessToken(token);
                Authentication authentication = jwtService.getAuthentication(claims);
                accessor.setUser(authentication);
            } catch (AuthException e) {
                throw new AccessDeniedException("Invalid or expired token", e);
            }
        }
        return message;
    }
}
