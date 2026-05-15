package com.example.chatapp.chat.room.dto.response;

import java.util.List;

public record CreateChatRoomResponse(
        Long id,
        String roomName,
        List<Long> participantIds
) {
}

