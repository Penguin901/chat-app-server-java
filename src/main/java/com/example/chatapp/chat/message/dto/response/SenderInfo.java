package com.example.chatapp.chat.message.dto.response;

import com.example.chatapp.user.User;

public record SenderInfo(
        Long userId,
        String nickname,
        String bio,
        String profileImageUrl
) {
    public static SenderInfo from(User user) {
        return new SenderInfo(
                user.getId(),
                user.getNickname(),
                user.getBio(),
                user.getProfileImageUrl()
        );
    }
}
