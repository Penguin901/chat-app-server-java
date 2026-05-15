package com.example.chatapp.auth.oauth;

import com.example.chatapp.user.dto.UserResponse;
import com.example.chatapp.common.exception.AuthException;
import com.example.chatapp.common.exception.ErrorCode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

/**
 * Google ID Token 검증 클래스
 * <p>
 * 클라이언트로부터 전달받은 Google의 ID Token을 검증하고 사용자 계정정보 생성에 필요한 데이터를 반환합니다.
 */
@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${google.web.client-id}") String googleClientIds) {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientIds))
                .setIssuers(Arrays.asList("https://accounts.google.com", "accounts.google.com"))
                .build();
    }

    public UserResponse verifiedToken(String idToken) {
        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken); // 잘못된 토큰 또는 만료된 토큰의 경우 null 반환

            if (googleIdToken == null) {
                throw new AuthException(ErrorCode.INVALID_OAUTH_TOKEN);
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            String oauthId = payload.getSubject();
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();

            OAuthUserInfo oAuthUserInfo = new GoogleOAuthUserInfo(oauthId, email, emailVerified);

            return UserResponse.builder()
                    .oauthId(oAuthUserInfo.getOauthId())
                    .email(oAuthUserInfo.getEmail())
                    .emailVerified(oAuthUserInfo.isEmailVerified())
                    .provider(oAuthUserInfo.getProvider()).build();

        } catch (GeneralSecurityException | IOException e) {
            throw new AuthException(ErrorCode.OAUTH_SERVER_ERROR);
        }
    }
}
