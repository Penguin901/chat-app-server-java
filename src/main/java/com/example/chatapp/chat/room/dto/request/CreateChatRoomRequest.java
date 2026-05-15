package com.example.chatapp.chat.room.dto.request;

import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CreateChatRoomRequest(
        String roomName,
        @NotNull
        @Size(min = 1)
        List<Long> participantIds
) {
}


