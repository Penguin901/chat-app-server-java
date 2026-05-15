package com.example.chatapp.chat.room.dto.response;

import com.example.chatapp.chat.room.ChatRoom;

import java.time.LocalDateTime;

public record ChatPreviewResponse(
        Long chatRoomId,
        String roomName,
        String profileImageUrl,
        String lastMessageContent,
        LocalDateTime lastMessageAt
) {
    public static ChatPreviewResponse from(ChatRoom chatRoom, String profileImageUrl) {
        return new ChatPreviewResponse(
                chatRoom.getId(),
                chatRoom.getRoomName(),
                profileImageUrl,
                chatRoom.getLastMessageContent(),
                chatRoom.getLastMessageAt()
        );
    }
}
