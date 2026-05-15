package com.example.chatapp.chat.message.dto.response;

import com.example.chatapp.chat.message.ChatMessage;

import java.time.LocalDateTime;

// HTTP 응답
public record ChatMessageResponse(
        Long messageId,
        Long chatRoomId,
        Long senderId,
        String messageContent,
        LocalDateTime sentAt
) {
    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getChatRoom().getId(),
                chatMessage.getSender().getId(),
                chatMessage.getMessageContent(),
                chatMessage.getSentAt()
        );
    }
}