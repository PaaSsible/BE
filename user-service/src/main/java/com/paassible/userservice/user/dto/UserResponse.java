package com.paassible.userservice.user.dto;

import com.paassible.userservice.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String nickname;
    private String email;
    private String university;
    private String major;
    private String profileImageUrl;
    private String role;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .university(user.getUniversity())
                .major(user.getMajor())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole().name())
                .build();
    }
}
