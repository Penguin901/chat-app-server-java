package com.example.chatapp.auth;

import com.example.chatapp.auth.domain.IssuedTokens;
import com.example.chatapp.auth.dto.LoginResponse;
import com.example.chatapp.auth.dto.AccountInfo;
import com.example.chatapp.user.dto.UserResponse;
import com.example.chatapp.auth.oauth.GoogleTokenVerifier;
import com.example.chatapp.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * OAuth 로그인 요청을 처리하고 사용자 계정 생성을 조회 또는 생성하여 인증에 필요한 토큰을 발급합니다.
 */
@Component
@RequiredArgsConstructor
public class LoginUseCase {

    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserService userService;
    private final AuthService authService;

    @Transactional
    public LoginResponse loginWithOAuth(String idToken) {
        UserResponse oAuthUser = googleTokenVerifier.verifiedToken(idToken);
        Long userId = userService.findOrCreateUserByOAuth(oAuthUser);

        IssuedTokens issuedTokens = authService.issueTokens(userId);

        AccountInfo accountInfo = new AccountInfo(userId, oAuthUser.getEmail());
        return new LoginResponse(
                issuedTokens.accessToken(),//issuedTokens.accessTokenExpiresAt(),
                issuedTokens.refreshToken(),//issuedTokens.refreshTokenExpiresAt(),
                accountInfo
        );
    }

}
