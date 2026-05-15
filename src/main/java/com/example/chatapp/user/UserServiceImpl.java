package com.example.chatapp.user;

import com.example.chatapp.friend.dto.SearchType;
import com.example.chatapp.user.dto.*;
import com.example.chatapp.common.exception.AuthException;
import com.example.chatapp.common.exception.ErrorCode;
import com.example.chatapp.common.exception.UserException;
import com.example.chatapp.auth.domain.RefreshTokenInfo;
import com.example.chatapp.user.dto.request.UpdateUsernameRequest;
import com.example.chatapp.user.dto.request.UserProfileRequest;
import com.example.chatapp.user.dto.response.UpdateUsernameResponse;
import com.example.chatapp.user.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public List<User> getUsersOrThrow(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        if (users.size() != userIds.size()) {
            throw new UserException(ErrorCode.USER_NOT_FOUND);
        }

        return users;
    }

    @Override
    public Long findOrCreateUserByOAuth(UserResponse oauthDTO) {
        if (oauthDTO.getProvider() == null) {
            throw new AuthException(ErrorCode.OAUTH_PROVIDER_MISMATCH);
        }

        // 기존 사용자 조회
        User existingUser = userRepository.findByOauthIdAndOauthProvider(oauthDTO.getOauthId(), oauthDTO.getProvider()).orElse(null);

        // 탈퇴한 회원인 경우
        if (existingUser != null) {
            if (existingUser.isDeleted()) {
                existingUser.reactivateAccount(
                        oauthDTO.getProvider(),
                        oauthDTO.getOauthId()
                );
                return existingUser.getId();
            }

            if (!existingUser.getOauthProvider().equals(oauthDTO.getProvider())) {
                throw new UserException(ErrorCode.USER_PROVIDER_MISMATCH);
            }

            return existingUser.getId();
        }

        // 사용자가 존재하지 않는 경우 새로운 사용자 생성
        User newUser = User.create(
                oauthDTO.getEmail(),
                oauthDTO.getOauthId(),
                oauthDTO.getProvider()
        );

        userRepository.save(newUser);
        return newUser.getId();
    }

    @Override
    @Transactional
    public void updateRefreshToken(Long currentUserId, RefreshTokenInfo refreshTokenInfo) {
        User user = getUserOrThrow(currentUserId);

        // RefreshTokenInfo에서 유효한 값인지 이미 검증함
        user.updateTokenInfo(refreshTokenInfo);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(Long currentUserId, UserProfileRequest request) {
        User user = getUserOrThrow(currentUserId);
        user.updateUserProfile(request.nickname(), request.bio(), request.profileImageUrl());
        return UserProfileResponse.from(user);
    }

    @Override
    @Transactional
    public UpdateUsernameResponse updateUsername(Long currentUserId, UpdateUsernameRequest request) {
        String trimmedUsername = request.username().trim();

        // 본인 제외 중복 확인
        if (!isUsernameAvailable(currentUserId, trimmedUsername)) {
            throw new UserException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        User user = getUserOrThrow(currentUserId);
        user.updateUsername(trimmedUsername);

        return new UpdateUsernameResponse(trimmedUsername);
    }

    @Override
    public Optional<User> searchUserExcludingSelf(Long currentUserId, SearchType searchType, String keyword) {
        String trimmedKeyword = keyword.trim().toLowerCase();

        return switch (searchType) {
            case EMAIL -> userRepository.findByEmailAndDeletedFalseAndIdNot(trimmedKeyword, currentUserId);
            case USERNAME -> userRepository.findByUsernameAndDeletedFalseAndIdNot(trimmedKeyword, currentUserId);
        };
    }

    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        User user = getUserOrThrow(userId);
        return UserProfileResponse.from(user);
    }

    @Override
    public boolean isUsernameAvailable(Long currentUserId, String username) {
        if (username == null || username.isBlank()) {
            throw new UserException(ErrorCode.INVALID_USERNAME);
        }

        return !userRepository.existsByUsernameAndIdNot(username, currentUserId);
    }

    @Override
    @Transactional
    public void deleteAccount(Long currentUserId) {
        User user = getUserOrThrow(currentUserId);
        user.deleteAccount();
    }
}
