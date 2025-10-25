package com.paassible.meetservice.meet.message;

import com.paassible.meetservice.client.board.BoardMemberResponse;
import java.util.List;

public record ParticipantStatusMessage(
        List<BoardMemberResponse> presentMembers,
        List<BoardMemberResponse> absentMembers
) {}
