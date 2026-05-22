package com.example.chatapp.user;

import com.example.chatapp.auth.domain.RefreshTokenInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * OAuth 로그인을 통해 생성된 사용자 계정 및 프로필 정보를 저장합니다.
 * <p>
 * Refresh Token을 통해 사용자의 인증정보를 관리합니다.
 */
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_username", columnNames = "username")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 계정 식별 정보 -> 이메일 검색 없애기
    @Column(length = 100, nullable = false)
    private String email;

    @Column(nullable = false)
    private String oauthId;

    @Column(nullable = false)
    private String oauthProvider;

    // 공개용 식별 정보
    @Column(length = 20)
    private String username;

    // 프로필 정보
    @Column(length = 20)
    private String nickname;  //빈 문자열로 초기화. 이후 이름 설정 화면에서 값 저장

    private String bio; //상태메세지

    private String profileImageUrl;

    // 토큰 정보
    private String refreshToken;

    private LocalDateTime refreshExpiration;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public User(String nickname, String bio, String profileImageUrl, String username, String email, String oauthId, String oauthProvider, String refreshToken, LocalDateTime createdAt, LocalDateTime refreshExpiration) {
        this.nickname = nickname;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.username = username;
        this.email = email;
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
        this.refreshToken = refreshToken;
        this.refreshExpiration = refreshExpiration;
    }

    private User(String email, String oauthId, String oauthProvider) {
        this.email = email;
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
        createdAt = LocalDateTime.now();
    }

    public static User create(String email, String oauthId, String provider) {
        return new User(email, oauthId, provider);
    }

    public void updateUserProfile(String newNickname, String bio, String profileImageUrl) {
        if (newNickname != null) {
            this.nickname = newNickname;
        }

        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateTokenInfo(RefreshTokenInfo refreshTokenInfo) {
        this.refreshToken = refreshTokenInfo.refreshToken();
        this.refreshExpiration = refreshTokenInfo.refreshExpiration();
    }

    public void reactivateAccount(String provider, String oauthId) {
        this.deleted = false;
        this.deletedAt = null;
        this.oauthProvider = provider;
        this.oauthId = oauthId;
    }

    // 현재는 계정 비활성화하는 것으로 탈퇴 처리
    public void deleteAccount() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}

