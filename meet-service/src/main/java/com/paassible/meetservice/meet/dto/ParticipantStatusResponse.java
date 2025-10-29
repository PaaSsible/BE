package com.paassible.meetservice.meet.dto;

import java.util.List;

public record ParticipantStatusResponse(
        Long userId,
        String userName,
        String profileImageUrl,
        Boolean isHostUser,
        List<AttendeeResponse> presentMembers,
        List<AttendeeResponse> absentMembers
) {}
