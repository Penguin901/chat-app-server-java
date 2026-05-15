package com.example.chatapp.chat.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    List<ChatMember> findByChatRoomIdAndActiveFalse(Long chatRoomId);

    Optional<ChatMember> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    boolean existsByChatRoomIdAndActiveTrue(Long chatRoomId);

    void deleteByChatRoomId(Long chatRoomId);
}
