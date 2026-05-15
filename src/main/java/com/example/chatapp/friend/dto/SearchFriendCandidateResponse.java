package com.example.chatapp.friend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SearchFriendCandidateResponse(
        boolean found,
        boolean alreadyFriend,
        Long userId,
        String nickname,
        String bio,
        String profileImageUrl
) {

    public static SearchFriendCandidateResponse notFound() {
        return new SearchFriendCandidateResponse(
                false, false, null, null, null, null);
    }

    public static SearchFriendCandidateResponse found(boolean alreadyFriend, Long userId, String nickname, String bio, String profileImageUrl) {
        return new SearchFriendCandidateResponse(
                true,
                alreadyFriend,
                userId,
                nickname,
                bio,
                profileImageUrl
        );
    }
}