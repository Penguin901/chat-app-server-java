package com.example.chatapp.chat.room;

import com.example.chatapp.chat.message.ChatMessage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * 사용자들의 대화 정보를 저장합니다.
 * <p>
 * DIRECT 타입(1대1채팅)의 경우 direct_room_key 유니크 제약 조건을 통해 동일한 사용자 간 중복 채팅방 생성을 방지합니다.
 * 마지막 메세지 정보(last_message_content, last_message_at)를 저장하여 메세지 테이블을 조회하지 않고 채팅방 목록을 정렬합니다.
 */
@Entity
@Table(name = "chat_rooms",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chat_rooms_direct_room_key", columnNames = {"direct_room_key"}) // 동일유저간 방 중복생성 방지
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    public enum RoomType {
        DIRECT,
        GROUP
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private RoomType roomType;

    @Column(length = 100)
    private String roomName;

    private String directRoomKey;

    private String lastMessageContent;

    private LocalDateTime lastMessageAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private ChatRoom(RoomType roomType, @Nullable String roomName, @Nullable String directRoomKey) {
        this.roomType = roomType;
        this.roomName = roomName;
        this.directRoomKey = directRoomKey;
        this.lastMessageContent = "";
        this.lastMessageAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public static ChatRoom createDirect(String pairId) {
        return new ChatRoom(RoomType.DIRECT, null, pairId);
    }

    public static ChatRoom createGroup(@Nullable String roomName) {
        return new ChatRoom(RoomType.GROUP, roomName, null);
    }

    public void updateLastMessageContent(ChatMessage message) {
        this.lastMessageContent = message.getMessageContent();
        this.lastMessageAt = message.getSentAt();
    }
}
