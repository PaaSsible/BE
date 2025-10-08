package com.paassible.meetservice.meet.event;

public record ParticipantLeftEvent(Long meetId, Long userId){}