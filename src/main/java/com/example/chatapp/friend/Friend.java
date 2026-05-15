package com.example.chatapp.friend;

import com.example.chatapp.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자간 친구 관계를 나타냅니다.
 * <p>
 * (user_id, friend_user_id) 복합 유니크 제약 조건을 통해 동일한 친구 관계가 중복으로 생성되는 것을 방지합니다.
 */
@Entity
@Table(
        name = "friends",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_id_friend_user_id", columnNames = {"user_id", "friend_user_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_user_id")
    private User friendUser;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Friend(User user, User friendUser) {
        this.user = user;
        this.friendUser = friendUser;
        createdAt = LocalDateTime.now();
    }

    public static Friend create(User user, User friendUser) {
        return new Friend(user, friendUser);
    }
}
