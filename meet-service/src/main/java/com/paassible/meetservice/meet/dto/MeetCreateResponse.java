package com.paassible.meetservice.meet.dto;

import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.entity.MeetingStatus;

import java.time.LocalDateTime;

public record MeetCreateResponse (
        Long meetId,
        Long boardId,
        Long hostId,
        MeetingStatus status,
        LocalDateTime startTime
){
    public static MeetCreateResponse from(Meet meet) {
        return new MeetCreateResponse(
                meet.getId(),
                meet.getBoardId(),
                meet.getHostId(),
                meet.getStatus(),
                meet.getStartTime()
        );
    }
    }
