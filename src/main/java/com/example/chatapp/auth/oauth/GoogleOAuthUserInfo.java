package com.example.chatapp.auth.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleOAuthUserInfo implements OAuthUserInfo {
    private final String oauthId;
    private final String email;
    private final boolean emailVerified;
    private final String provider = "GOOGLE";
}

