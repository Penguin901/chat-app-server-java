package com.example.chatapp.chat.message;

import com.example.chatapp.chat.message.dto.response.ChatMessageResponse;
import com.example.chatapp.chat.member.ChatMember;
import com.example.chatapp.chat.room.ChatRoom;
import com.example.chatapp.common.exception.ChatRoomException;
import com.example.chatapp.common.exception.ErrorCode;
import com.example.chatapp.chat.member.ChatMemberRepository;
import com.example.chatapp.chat.room.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemberRepository chatMemberRepository;

    public List<ChatMessageResponse> getChatMessages(Long currentUserId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ChatRoomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatMember chatMember =
                chatMemberRepository.findByChatRoomIdAndUserId(chatRoomId, currentUserId)
                        .orElseThrow(() -> new ChatRoomException(ErrorCode.NOT_A_MEMBER));


        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdAndSentAtAfter(chatRoom.getId(), chatMember.getJoinedAt());

        return chatMessages.stream()
                .map(ChatMessageResponse::from)
                .toList();
    }
}
