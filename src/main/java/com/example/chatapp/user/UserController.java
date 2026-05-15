package com.example.chatapp.user;

import com.example.chatapp.security.UserPrincipal;
import com.example.chatapp.user.dto.request.UpdateUsernameRequest;
import com.example.chatapp.user.dto.request.UserProfileRequest;
import com.example.chatapp.user.dto.response.UpdateUsernameResponse;
import com.example.chatapp.user.dto.response.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me/profile")
    public UserProfileResponse getMyProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal.userId();
        return userService.getUserProfile(currentUserId);
    }

    // 다른 유저 프로필 가져오기
    @GetMapping("/{userId}/profile")
    public UserProfileResponse getUserProfile(@PathVariable long userId) {
        return userService.getUserProfile(userId);
    }

    // 사용자 아이디 중복 조회
    @GetMapping("/username/availability")
    public boolean isUsernameAvailable(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam String username) {
        Long currentUserId = userPrincipal.userId();
        return userService.isUsernameAvailable(currentUserId, username);
    }

    // 사용자 프로필 수정
    @PutMapping("/me/profile")
    public UserProfileResponse updateUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody UserProfileRequest request) { //HttpServletRequest 객체는 요청의 헤더, 파라미터, 세션 등의 정보 포함
        Long currentUserId = userPrincipal.userId();
        return userService.updateUserProfile(currentUserId, request);
    }

    // 사용자 아이디 수정
    @PutMapping("/me/username")
    public UpdateUsernameResponse updateUsername(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody UpdateUsernameRequest request) { //HttpServletRequest 객체는 요청의 헤더, 파라미터, 세션 등의 정보 포함
        Long currentUserId = userPrincipal.userId();
        return userService.updateUsername(currentUserId, request);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal.userId();
        userService.deleteAccount(currentUserId);
        return ResponseEntity.noContent().build();
    }
}

