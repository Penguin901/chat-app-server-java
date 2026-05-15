package com.example.chatapp.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record UserPrincipal(
        Long userId,
        Collection<? extends GrantedAuthority> authorities
) {
}