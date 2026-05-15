package com.example.chatapp.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByOauthIdAndOauthProvider(String oauthId, String oauthProvider);

    // 친구 추가 전 해당 이메일의 사용자 있는지 조회
    Optional<User> findByEmailAndDeletedFalseAndIdNot(String email, Long userid);

    // 친구 추가 전 해당 계정아이디의 사용자 있는지 조회
    Optional<User> findByUsernameAndDeletedFalseAndIdNot(String username, Long userid);

    // 계정 아이디 조회 (계정 아이디 추가전 중복검사)
    boolean existsByUsernameAndIdNot(String username, Long userid);
}
