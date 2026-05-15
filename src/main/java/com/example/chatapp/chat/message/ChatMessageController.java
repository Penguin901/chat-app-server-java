package com.example.chatapp.chat.message;

import com.example.chatapp.chat.message.dto.request.SendMessageRequest;
import com.example.chatapp.security.UserPrincipal;
import com.example.chatapp.chat.message.dto.response.ChatMessageResponse;
import com.example.chatapp.chat.message.dto.response.SendMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageUseCase chatMessageUseCase;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/chat-messages/{chatRoomId}")
    public List<ChatMessageResponse> getChatMessages(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long chatRoomId) {
        Long currentUserId = userPrincipal.userId();
        return chatMessageService.getChatMessages(currentUserId, chatRoomId);
    }

    @MessageMapping("message")
    public void sendMessage(Authentication authentication, @Payload SendMessageRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        SendMessageResponse event = chatMessageUseCase.handleSendMessage(userPrincipal.userId(), request);
        simpMessagingTemplate.convertAndSend(
                "/sub/chatroom/" + request.chatRoomId(),
                event
        );
    }
}