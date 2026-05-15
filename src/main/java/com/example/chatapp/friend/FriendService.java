package com.example.chatapp.friend;

import com.example.chatapp.user.User;
import com.example.chatapp.user.dto.response.UserProfileResponse;

import java.util.List;

public interface FriendService {

    List<UserProfileResponse> getFriends(Long currentUserId);

    User addFriend(User currentUser, User targetUser); // 유저아이디로 받을지 고민

    void removeFriend(Long currentUserId, Long targetUserId);
}
