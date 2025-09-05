package com.paassible.meetservice.meet.dto;

import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.entity.MeetingStatus;
import com.paassible.meetservice.meet.entity.Participant;

import java.time.LocalDateTime;

public record MeetCreateResponse (
        Long meetId,
        Long boardId,
        Long hostId,
        Long participantId,
        MeetingStatus status,
        LocalDateTime startTime
){
    public static MeetCreateResponse from(Meet meet, Participant participant) {
        return new MeetCreateResponse(
                meet.getId(),
                meet.getBoardId(),
                meet.getHostId(),
                participant.getId(),
                meet.getStatus(),
                meet.getStartTime()
        );
    }
    }
