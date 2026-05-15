package com.example.chatapp.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

//목적에 맞게 dto 분리하기
@Getter
public class UserRequest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nickname;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bio;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String profileImageUrl;
    @NotBlank()
    @Size(min = 4, max = 20)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;

    @Builder
    public UserRequest(String nickname, String bio, String profileImageUrl, String username) {
        this.nickname = nickname;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.username = username;
    }
}
