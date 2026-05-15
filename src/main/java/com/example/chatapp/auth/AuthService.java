package com.example.chatapp.auth;

import com.example.chatapp.auth.domain.IssuedTokens;
import com.example.chatapp.auth.dto.RefreshTokenResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface AuthService {

    IssuedTokens issueTokens(Long userId);

    RefreshTokenResponse reissueToken(String refreshToken) throws GeneralSecurityException, IOException;
}
