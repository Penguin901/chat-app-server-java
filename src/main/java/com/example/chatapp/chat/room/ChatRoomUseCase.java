package com.example.chatapp.chat.room;

import com.example.chatapp.common.exception.ChatRoomException;
import com.example.chatapp.common.exception.ErrorCode;
import com.example.chatapp.common.exception.UserException;
import com.example.chatapp.chat.room.dto.request.CreateChatRoomRequest;
import com.example.chatapp.chat.room.dto.response.CreateChatRoomResponse;
import com.example.chatapp.user.User;
import com.example.chatapp.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ChatRoomUseCase {

    private final UserService userService;
    private final ChatRoomService chatRoomService;

    public CreateChatRoomResponse getOrCreateChatRoom(Long requesterId, CreateChatRoomRequest createChatRoomRequest) {
        List<Long> participantIds = createChatRoomRequest.participantIds();

        validateParticipants(participantIds, requesterId);

        List<Long> allUserIds = new ArrayList<>(participantIds);
        allUserIds.add(requesterId);

        List<User> users = userService.getUsersOrThrow(allUserIds);
        ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(users, createChatRoomRequest.roomName());

        return new CreateChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getRoomName(),
                users.stream().map(User::getId).toList()
        );
    }

    @Transactional
    public void leaveChatRoom(Long userId, Long chatRoomId) {
        //사용자가 방을 나감(해당 룸의 멤버 비활성화 또는 삭제)
        chatRoomService.leaveChatRoom(userId, chatRoomId);

        //멤버 삭제 후 해당 방에 멤버 없는 경우 해당 룸의 메세지 삭제 후  방 삭제
        chatRoomService.deleteRoomIfNoActiveMembers(chatRoomId);
    }

    private void validateParticipants(List<Long> participantIds, Long requesterId) {
        if (participantIds == null || participantIds.isEmpty()) {
            throw new ChatRoomException(ErrorCode.INVALID_PARTICIPANTS_COUNT);
        }

        if (participantIds.stream().anyMatch(Objects::isNull)) {
            throw new UserException(ErrorCode.INVALID_PARTICIPANTS_COUNT);
        }

        // 중복포함된 유저 있는지 확인
        if (participantIds.size() != new HashSet<>(participantIds).size()) {
            throw new UserException(ErrorCode.DUPLICATE_PARTICIPANTS);
        }

        // 자기자신 포함 확인
        if (participantIds.contains(requesterId)) {
            throw new ChatRoomException(ErrorCode.CANNOT_ADD_SELF_AS_PARTICIPANT);
        }
    }
}
