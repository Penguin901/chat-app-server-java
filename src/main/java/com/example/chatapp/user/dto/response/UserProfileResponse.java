package com.example.chatapp.user.dto.response;

import com.example.chatapp.user.User;

public record UserProfileResponse(
        Long id,
        String nickname,
        String bio,
        String profileImageUrl
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getBio(),
                user.getProfileImageUrl()
        );
    }
}

