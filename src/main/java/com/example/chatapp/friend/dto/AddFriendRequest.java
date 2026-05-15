package com.example.chatapp.friend.dto;

import jakarta.validation.constraints.NotNull;

public record AddFriendRequest(
        @NotNull()
        Long targetUserId
) {
}