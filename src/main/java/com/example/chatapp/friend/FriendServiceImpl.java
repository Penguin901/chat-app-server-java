package com.example.chatapp.friend;

import com.example.chatapp.user.dto.response.UserProfileResponse;
import com.example.chatapp.user.User;
import com.example.chatapp.common.exception.ErrorCode;
import com.example.chatapp.common.exception.FriendException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;

    public List<UserProfileResponse> getFriends(Long currentUserId) {
        List<Friend> friends = friendRepository.findByUserId(currentUserId);

        return friends.stream()
                .map(Friend::getFriendUser)
                .map(UserProfileResponse::from).toList();
    }

    @Override
    @Transactional
    public User addFriend(User currentUser, User targetUser) {
        if (friendRepository.existsByUserIdAndFriendUserId(currentUser.getId(), targetUser.getId())) { //친구관계가 존재할 때
            throw new FriendException(ErrorCode.FRIEND_ALREADY_EXISTS);
        }

        Friend friend = Friend.create(currentUser, targetUser);
        friendRepository.save(friend);

        return targetUser;
    }

    @Override
    @Transactional
    public void removeFriend(Long currentUserId, Long targetUserId) {
        friendRepository.deleteByUserIdAndFriendUserId(currentUserId, targetUserId);
    }
}
