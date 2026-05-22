package com.example.chatapp.chat.member;

import com.example.chatapp.chat.room.ChatRoom;
import com.example.chatapp.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 특정 채팅방에 참여한 사용자와 채팅방 간의 관계를 나타냅니다.
 * <p>
 * activate 필드를 통해 입장 및 퇴장 상태를 관리하며,
 * (chat_room_id, user_id) 복합 유니크 제약 조건을 통해 동일한 사용자가 같은 채팅방에 중복 참여하는 것을 방지합니다.
 */
@Entity
@Table(name = "chat_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chat_members_chat_room_id_user_id", columnNames = {"chat_room_id", "user_id"}), //현재 방의 멤버 찾기
        },
        indexes = {
                @Index(name = "idx_chat_members_user_id_active", columnList = "user_id, active") // 사용자가 참여중인 방 조회
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMember {

    @EmbeddedId
    private ChatMemberId id = new ChatMemberId();

    @MapsId("chatRoomId") //외캐키를 주키로
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean active;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private ChatMember(ChatRoom chatRoom, User user) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.active = true;
        this.joinedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public static ChatMember create(ChatRoom chatRoom, User user) {
        return new ChatMember(chatRoom, user);
    }

    public void activate() {
        if (!this.active) {
            this.active = true;
            this.joinedAt = LocalDateTime.now();
        }
    }

    public void deactivate() {
        if (this.active) this.active = false;
    }
}
