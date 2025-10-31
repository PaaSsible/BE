package com.paassible.meetservice.meet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
                timezone = "Asia/Seoul")
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
