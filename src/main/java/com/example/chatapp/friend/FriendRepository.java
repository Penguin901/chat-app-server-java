package com.example.chatapp.friend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findByUserId(Long userId);

    // 친구관계 존재하는지 확인
    boolean existsByUserIdAndFriendUserId(Long userId, Long friendUserId);

    default boolean isFriend(Long userId, Long targetUserId) {
        return existsByUserIdAndFriendUserId(userId, targetUserId);
    }

    @Modifying
    @Query("""
            delete from Friend f
            where  f.user.id  = :userId and f.friendUser.id = :friendUserId
            """)
    void deleteByUserIdAndFriendUserId(@Param("userId") Long userId,
                                       @Param("friendUserId") Long friendUserId
    );
}
