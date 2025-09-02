package com.paassible.meetservice.client.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String nickname;
    private String email;
    private String major;
    private String profileImageUrl;
    private String role;
}
