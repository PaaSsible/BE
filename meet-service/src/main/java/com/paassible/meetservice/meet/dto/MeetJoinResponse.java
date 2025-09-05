package com.paassible.meetservice.meet.dto;

import com.paassible.meetservice.meet.entity.Participant;

public record MeetJoinResponse (
        Long participantId
){
    public static MeetJoinResponse from(Participant participant) {
        return new MeetJoinResponse(
                participant.getId()
        );
    }
}
