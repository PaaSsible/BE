package com.paassible.meetservice.meet.dto;

import java.time.LocalDateTime;

public record MeetCreateRequest (
        Long boardId,
        LocalDateTime startTime
){}
