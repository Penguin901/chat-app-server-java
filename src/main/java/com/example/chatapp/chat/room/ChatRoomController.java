package com.example.chatapp.chat.room;

import com.example.chatapp.security.UserPrincipal;
import com.example.chatapp.chat.room.dto.response.ChatPreviewResponse;
import com.example.chatapp.chat.room.dto.request.CreateChatRoomRequest;
import com.example.chatapp.chat.room.dto.response.CreateChatRoomResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomUseCase chatRoomUseCase;
    private final ChatRoomService chatRoomService;

    @GetMapping
    List<ChatPreviewResponse> getChatRooms(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal.userId();
        return chatRoomService.getChatRoomsPreview(currentUserId);

    }

    @PostMapping
    CreateChatRoomResponse getOrCreateChatRoom(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody CreateChatRoomRequest request) {
        Long currentUserId = userPrincipal.userId();
        return chatRoomUseCase.getOrCreateChatRoom(currentUserId, request);
    }

    @DeleteMapping("/{chatRoomId}/members/me")
    ResponseEntity<Void> leaveChatRoom(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("chatRoomId") Long chatRoomId) {
        Long currentUserId = userPrincipal.userId();
        chatRoomUseCase.leaveChatRoom(currentUserId, chatRoomId);
        return ResponseEntity.noContent().build();
    }
}
