package com.example.chatapp.auth.oauth;

public interface OAuthUserInfo {

    String getOauthId();

    String getEmail();

    boolean isEmailVerified();

    String getProvider();
}
