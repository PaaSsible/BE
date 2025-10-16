package com.paassible.meetservice.meet.dto;

import com.paassible.meetservice.client.board.BoardMemberResponse;
import com.paassible.meetservice.meet.entity.Meet;

import java.util.List;

public record MeetOngoingResponse(
        Long meetId,
        Integer participantCount,
        List<AttendeeResponse> attendees

) {
    public static MeetOngoingResponse from(Meet meet, List<BoardMemberResponse> attendees) {
        List<AttendeeResponse> attendeeList = attendees.stream()
                .map(a -> new AttendeeResponse(a.getUserId(), a.getUserName(), a.getProfileImageUrl()))
                .toList();

        return new MeetOngoingResponse(
                meet.getId(),
                meet.getParticipantCount(),
                attendeeList
        );
    }
}
