package com.paassible.meetservice.meet.event;

import com.paassible.meetservice.meet.entity.Meet;

public record ParticipantJoinedEvent(Meet meet, Long userId) {}
