package com.example.chatapp.friend;

import com.example.chatapp.common.exception.ErrorCode;
import com.example.chatapp.common.exception.FriendException;
import com.example.chatapp.common.exception.UserException;
import com.example.chatapp.friend.dto.SearchFriendCandidateResponse;
import com.example.chatapp.friend.dto.SearchType;
import com.example.chatapp.user.User;
import com.example.chatapp.user.UserService;
import com.example.chatapp.user.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FriendUseCase {

    private final UserService userService;
    private final FriendService friendService;
    private final FriendRepository friendRepository;

    public SearchFriendCandidateResponse searchFriendCandidate(Long currentUserId, SearchType searchType, String keyword) {
        if (keyword.trim().length() < 4) {
            throw new UserException(ErrorCode.INVALID_SEARCH_KEYWORD);
        }

        User targetUser = userService.searchUserExcludingSelf(currentUserId, searchType, keyword).orElse(null);

        if (targetUser == null) return SearchFriendCandidateResponse.notFound();

        boolean alreadyFriend = friendRepository.isFriend(currentUserId, targetUser.getId());

        return SearchFriendCandidateResponse.found(alreadyFriend, targetUser.getId(), targetUser.getNickname(), targetUser.getBio(), targetUser.getProfileImageUrl());
    }

    public UserProfileResponse addFriend(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new FriendException(ErrorCode.SELF_FRIEND_NOT_ALLOWED);
        }

        List<User> users = userService.getUsersOrThrow(List.of(currentUserId, targetUserId));

        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user
                ));

        User currentUser = userMap.get(currentUserId);
        User targetUser = userMap.get(targetUserId);

        User friendUser = friendService.addFriend(currentUser, targetUser);

        return UserProfileResponse.from(friendUser);
    }
}
