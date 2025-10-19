package com.paassible.meetservice.meet.event;

import com.paassible.meetservice.meet.entity.Meet;

public record ParticipantLeftEvent(
        Long meetId,
        Long boardId,
        Long userId){}