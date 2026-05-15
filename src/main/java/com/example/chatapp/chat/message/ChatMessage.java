package com.example.chatapp.chat.message;

import com.example.chatapp.chat.room.ChatRoom;
import com.example.chatapp.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 특정 채팅방에 속한 메세지를 저장합니다.
 * <p>
 * (chat_room_id, sent_at) 복합인덱스로 최근 메세지부터 조회합니다.
 */
@Entity
@Table(name = "chat_messages",
        indexes = {
                @Index(name = "idx_chat_room_sent_at", columnList = "chat_room_id, sent_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false)
    private String messageContent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    private ChatMessage(ChatRoom chatRoom, User sender, String messageContent) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.messageContent = messageContent;
        sentAt = LocalDateTime.now();
    }

    public static ChatMessage create(ChatRoom chatRoom, User sender, String messageContent) {
        return new ChatMessage(chatRoom, sender, messageContent);

    }
}
