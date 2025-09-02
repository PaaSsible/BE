package com.paassible.meetservice.meet.service;

import com.paassible.meetservice.client.board.BoardClient;
import com.paassible.meetservice.meet.dto.MeetCreateRequest;
import com.paassible.meetservice.meet.dto.MeetCreateResponse;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.repository.MeetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetService {

    private final MeetRepository meetRepository;
    private final BoardClient boardClient;

    public MeetCreateResponse createMeet(Long hostId, MeetCreateRequest request) {

       boardClient.validateBoard(request.boardId());
       boardClient.validateUserInBoard(request.boardId(), hostId);

        Meet meet = Meet.create(
                request.boardId(),
                hostId,
                request.startTime() != null ? request.startTime() : LocalDateTime.now()
        );

        Meet savedMeet = meetRepository.save(meet);

        return MeetCreateResponse.from(savedMeet);
    }
}
