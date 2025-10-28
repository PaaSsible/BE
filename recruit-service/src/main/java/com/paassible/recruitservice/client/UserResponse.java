package com.paassible.recruitservice.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String nickname;
    private String email;
    private String university;
    private String major;
    private String profileImageUrl;
    private String role;
    private String positionName;
    private List<String> stackNames;
}
