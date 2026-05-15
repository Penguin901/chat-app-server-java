package com.example.chatapp.user;

import com.example.chatapp.friend.dto.SearchType;
import com.example.chatapp.user.dto.*;
import com.example.chatapp.auth.domain.RefreshTokenInfo;
import com.example.chatapp.user.dto.request.UpdateUsernameRequest;
import com.example.chatapp.user.dto.request.UserProfileRequest;
import com.example.chatapp.user.dto.response.UpdateUsernameResponse;
import com.example.chatapp.user.dto.response.UserProfileResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User getUserOrThrow(Long userId);

    List<User> getUsersOrThrow(List<Long> userIds);

    Long findOrCreateUserByOAuth(UserResponse oauthDTO);

    void updateRefreshToken(Long currentUserId, RefreshTokenInfo refreshTokenInfo);

    UserProfileResponse updateUserProfile(Long currentUserId, UserProfileRequest request);

    UpdateUsernameResponse updateUsername(Long currentUserId, UpdateUsernameRequest request);

    Optional<User> searchUserExcludingSelf(Long currentUserId, SearchType searchType, String keyword);

    UserProfileResponse getUserProfile(Long userId);

    boolean isUsernameAvailable(Long currentUserId, String username);

    void deleteAccount(Long currentUserId);
}
