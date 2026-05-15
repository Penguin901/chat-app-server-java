package com.example.chatapp.chat.message.dto.request;

// 웹소켓 요청
public record SendMessageRequest(
        Long chatRoomId,
        String messageContent
) {
}

