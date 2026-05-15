package com.example.chatapp.friend;

import com.example.chatapp.friend.dto.AddFriendRequest;
import com.example.chatapp.security.UserPrincipal;
import com.example.chatapp.friend.dto.SearchFriendCandidateResponse;
import com.example.chatapp.friend.dto.SearchType;
import com.example.chatapp.user.dto.response.UserProfileResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@Validated
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    private final FriendUseCase friendUseCase;

    @GetMapping
    public List<UserProfileResponse> getMyFriends(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal.userId();
        return friendService.getFriends(currentUserId);
    }

    // 친구로 등록할 사용자 검색
    @GetMapping("/search")
    public SearchFriendCandidateResponse searchFriendCandidate(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam SearchType searchType, @RequestParam @NotBlank String keyword) {
        Long currentUserId = userPrincipal.userId();
        return friendUseCase.searchFriendCandidate(currentUserId, searchType, keyword);
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> addFriend(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody AddFriendRequest request) {
        Long currentUserId = userPrincipal.userId();
        UserProfileResponse friendProfile = friendUseCase.addFriend(currentUserId, request.targetUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(friendProfile); // 201
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long friendId) {
        Long userId = userPrincipal.userId();
        friendService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build(); // 204
    }
}