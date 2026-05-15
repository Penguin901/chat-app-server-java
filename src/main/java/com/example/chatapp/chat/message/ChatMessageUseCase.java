package com.example.chatapp.chat.message;

import com.example.chatapp.chat.message.dto.request.SendMessageRequest;
import com.example.chatapp.chat.room.ChatRoom;
import com.example.chatapp.chat.room.ChatRoomService;
import com.example.chatapp.chat.message.dto.response.SendMessageResponse;
import com.example.chatapp.user.User;
import com.example.chatapp.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ChatMessageUseCase {

    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final ChatMessageRepository chatMessageRepository;

    public SendMessageResponse handleSendMessage(Long senderId, @Payload SendMessageRequest request) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(request.chatRoomId());
        User sender = userService.getUserOrThrow(senderId);

        String messageContent = request.messageContent();

        chatRoomService.validateMember(chatRoom.getId(), sender.getId());

        ChatMessage chatMessage = ChatMessage.create(chatRoom, sender, messageContent);
        chatMessageRepository.save(chatMessage);

        chatRoom.updateLastMessageContent(chatMessage);

        chatRoomService.activateInactiveMembers(chatRoom.getId()); // 그룹은 그냥 추가, 삭제하면 된다.

        return SendMessageResponse.from(chatMessage);
    }
}
