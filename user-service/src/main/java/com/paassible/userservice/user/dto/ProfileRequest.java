package com.paassible.userservice.user.dto;

import lombok.Getter;

@Getter
public class ProfileRequest {
    private String nickname;
    private String university;
    private String major;
}