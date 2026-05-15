package com.example.chatapp.chat.message.dto.response;

import com.example.chatapp.chat.message.ChatMessage;

import java.time.LocalDateTime;

// 웹소켓 응답
public record SendMessageResponse(
        Long messageId,
        Long chatRoomId,
        SenderInfo sender,
        String messageContent,
        LocalDateTime sentAt
) {
    public static SendMessageResponse from(ChatMessage chatMessage) {
        return new SendMessageResponse(
                chatMessage.getId(),
                chatMessage.getChatRoom().getId(),
                SenderInfo.from(chatMessage.getSender()),
                chatMessage.getMessageContent(),
                chatMessage.getSentAt()
        );
    }
}
