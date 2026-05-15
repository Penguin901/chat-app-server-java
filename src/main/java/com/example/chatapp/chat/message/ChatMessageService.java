package com.example.chatapp.chat.message;

import com.example.chatapp.chat.message.dto.response.ChatMessageResponse;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessageResponse> getChatMessages(Long currentUserId, Long chatRoomId);
}
