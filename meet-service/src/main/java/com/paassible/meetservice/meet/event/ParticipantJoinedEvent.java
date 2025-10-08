package com.paassible.meetservice.meet.event;

public record ParticipantJoinedEvent(Long meetId, Long userId) {}
