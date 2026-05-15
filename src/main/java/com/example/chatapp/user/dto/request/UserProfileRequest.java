package com.example.chatapp.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileRequest(
        @NotBlank String nickname,
        @Size(max = 255) String bio,
        String profileImageUrl
) {
}


