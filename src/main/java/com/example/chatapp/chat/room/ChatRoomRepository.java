package com.example.chatapp.chat.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
                SELECT cm.chatRoom
                FROM ChatMember cm
                WHERE cm.user.id = :userId
                  AND cm.active = TRUE
            """)
    List<ChatRoom> findActiveChatRoomsByUserId(Long userId);

    ChatRoom findByDirectRoomKey(String directRoomKey);
}