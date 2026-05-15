package com.example.chatapp.chat.room;

import com.example.chatapp.chat.member.ChatMember;
import com.example.chatapp.chat.member.ChatMemberRepository;
import com.example.chatapp.chat.message.ChatMessageRepository;
import com.example.chatapp.chat.room.dto.response.ChatPreviewResponse;
import com.example.chatapp.common.exception.ChatRoomException;
import com.example.chatapp.common.exception.ErrorCode;
import com.example.chatapp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public List<ChatPreviewResponse> getChatRoomsPreview(Long currentUserId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findActiveChatRoomsByUserId(currentUserId);

        return chatRooms.stream().map(chatRoom ->
                ChatPreviewResponse.from(chatRoom, null)
        ).toList();
    }

    @Override   // 기존 채팅방이 존재하는 경우
    public ChatRoom getChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ChatRoomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

    }

    @Override
    public ChatRoom getOrCreateChatRoom(List<User> users, @Nullable String roomName) {
        if (users.size() == 2) {
            return getOrCreateDirectChatRoom(users);
        }

        ChatRoom chatRoom = ChatRoom.createGroup(roomName);
        chatRoomRepository.save(chatRoom);
        addChatRoomMembers(chatRoom, users);

        return chatRoom;
    }


    public void validateMember(Long chatRoomId, Long senderId) {
        boolean isMember = chatMemberRepository.existsByChatRoomIdAndUserId(chatRoomId, senderId);
        if (!isMember) {
            throw new ChatRoomException(ErrorCode.NOT_A_MEMBER);
        }
    }

    public void activateInactiveMembers(Long chatRoomId) {
        List<ChatMember> inactiveMembers =
                chatMemberRepository.findByChatRoomIdAndActiveFalse(chatRoomId);

        for (ChatMember member : inactiveMembers) {
            member.activate();
        }
    }

    public void leaveChatRoom(Long currentUserId, Long chatRoomId) {
        ChatMember member = chatMemberRepository
                .findByChatRoomIdAndUserId(chatRoomId, currentUserId)
                .orElseThrow(() -> new ChatRoomException(ErrorCode.NOT_A_MEMBER));

        ChatRoom chatRoom = getChatRoom(chatRoomId);

        // 1대1 채팅 - 비활성화(동일 사용자와 방 재생성시 기존 방 사용하기 위해)
        if (chatRoom.getRoomType() == ChatRoom.RoomType.DIRECT) {
            member.deactivate();
        } else { // 그룹채팅 - 사용자가 방을 나가면 멤버에서 삭제
            chatMemberRepository.delete(member);
        }
    }

    public void deleteRoomIfNoActiveMembers(Long chatRoomId) {
        boolean hasActiveMember =
                chatMemberRepository.existsByChatRoomIdAndActiveTrue(chatRoomId);

        if (!hasActiveMember) {
            chatMemberRepository.deleteByChatRoomId(chatRoomId);
            chatMessageRepository.deleteByChatRoomId(chatRoomId);
            chatRoomRepository.deleteById(chatRoomId);
        }
    }

    private ChatRoom getOrCreateDirectChatRoom(List<User> users) {
        User user1 = users.get(0);
        User user2 = users.get(1);

        String directRoomKey = generateDirectRoomKey(user1.getId(), user2.getId());
        ChatRoom existingChatRoom = chatRoomRepository.findByDirectRoomKey(directRoomKey);

        if (existingChatRoom != null) return existingChatRoom;

        // TODO: 두명의 사용자가 동시에 생성 요청하는 경우(directRoomKey 제약 조건 위반) 처리 필요
        ChatRoom chatRoom = ChatRoom.createDirect(directRoomKey);
        chatRoomRepository.saveAndFlush(chatRoom);
        addChatRoomMembers(chatRoom, users);
        return chatRoom;

    }

    private String generateDirectRoomKey(Long userId1, Long userId2) {
        if (userId1.equals(userId2)) {
            throw new ChatRoomException(ErrorCode.CANNOT_ADD_SELF_AS_PARTICIPANT);
        }

        long min = Math.min(userId1, userId2);
        long max = Math.max(userId1, userId2);

        return min + "_" + max;
    }

    private void addChatRoomMembers(ChatRoom chatRoom, List<User> participants) {
        List<ChatMember> newMembers = participants.stream()
                .map(user -> ChatMember.create(chatRoom, user))
                .toList();

        chatMemberRepository.saveAll(newMembers);
    }
}
