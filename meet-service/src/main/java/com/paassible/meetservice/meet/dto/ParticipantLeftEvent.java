package com.paassible.meetservice.meet.dto;

import com.paassible.meetservice.meet.entity.Meet;

public record ParticipantLeftEvent (Meet meet, Long userId) {
}
