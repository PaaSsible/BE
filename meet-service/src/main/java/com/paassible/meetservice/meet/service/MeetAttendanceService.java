package com.paassible.meetservice.meet.service;

import com.paassible.meetservice.meet.repository.MeetRepository;
import com.paassible.meetservice.meet.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetAttendanceService {

    private final MeetRepository meetRepository;
    private final ParticipantRepository participantRepository;

    @Transactional(readOnly = true)
    public double calculateAttendanceRate(Long boardId, Long userId) {
        Long totalMeetCount = meetRepository.countByBoardId(boardId);
        if (totalMeetCount == 0) {
            return 0.0; // 회의가 하나도 없으면 0%
        }

        Long attendedMeetCount = participantRepository.countUserMeetParticipation(boardId, userId);

        return (double) attendedMeetCount / totalMeetCount * 100.0;
    }
}

