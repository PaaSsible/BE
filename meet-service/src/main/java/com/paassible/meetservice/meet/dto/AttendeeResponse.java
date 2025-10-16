package com.paassible.meetservice.meet.dto;

public record AttendeeResponse(
        Long userId,
        String userName,
        String profileImageUrl
){}
