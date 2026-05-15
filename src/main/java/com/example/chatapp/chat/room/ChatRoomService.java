package com.example.chatapp.chat.room;

import com.example.chatapp.chat.room.dto.response.ChatPreviewResponse;
import com.example.chatapp.user.User;

import javax.annotation.Nullable;
import java.util.List;

public interface ChatRoomService {

    List<ChatPreviewResponse> getChatRoomsPreview(Long currentUserId);

    ChatRoom getChatRoom(Long chatRoomId);

    ChatRoom getOrCreateChatRoom(List<User> users, @Nullable String roomName);

    void validateMember(Long chatRoomId, Long senderId);

    void activateInactiveMembers(Long chatRoomId);

    void leaveChatRoom(Long currentUserId, Long chatRoomId);

    void deleteRoomIfNoActiveMembers(Long chatRoomId);
}
