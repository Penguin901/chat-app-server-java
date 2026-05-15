package com.example.chatapp.chat.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(value = """
                select msg
                from ChatMessage msg
                where msg.chatRoom.id = :chatRoomId
                  and msg.sentAt >= :joinedAt
                order by msg.sentAt desc
            """)
    List<ChatMessage> findByChatRoomIdAndSentAtAfter(Long chatRoomId, LocalDateTime joinedAt);

    void deleteByChatRoomId(Long chatRoomId);

}
