package com.example.chatapp.auth;

import com.example.chatapp.auth.dto.LoginRequest;
import com.example.chatapp.auth.dto.LoginResponse;
import com.example.chatapp.auth.dto.RefreshTokenRequest;
import com.example.chatapp.auth.dto.RefreshTokenResponse;
import com.example.chatapp.common.exception.AuthException;
import com.example.chatapp.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateAndReturnToken(@RequestBody LoginRequest receivedToken) {
        LoginResponse loginResponse = loginUseCase.loginWithOAuth(receivedToken.idToken());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> reissueToken(@RequestBody RefreshTokenRequest request) {
        try {
            RefreshTokenResponse response = authService.reissueToken(request.refreshToken());
            return ResponseEntity.ok(response);
        } catch (GeneralSecurityException | IOException e) {
            throw new AuthException(ErrorCode.OAUTH_SERVER_ERROR);
        }
    }
}
