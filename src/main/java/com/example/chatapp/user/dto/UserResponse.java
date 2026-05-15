package com.example.chatapp.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 코틀린버전에서는 용도에 따라 여러개의 클래스로 분리한 후 이 클래스는 제거함
@Getter
public class UserResponse {

    private final Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nickname;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bio;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String profileImageUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String oauthId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String provider;
    private final boolean emailVerified;

    @Builder
    public UserResponse(Long id, String nickname, String bio, String profileImageUrl, String username, String email, String oauthId, String provider, String refreshToken, LocalDateTime refreshExpiration, boolean emailVerified) {
        this.id = id;
        this.nickname = nickname;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.username = username;
        this.oauthId = oauthId;
        this.email = email;
        this.emailVerified = emailVerified;
        this.provider = provider;
    }
}
