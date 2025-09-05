package com.paassible.meetservice.meet.repository;

import com.paassible.meetservice.meet.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    boolean existsByMeetIdAndUserId(Long meetId, Long userId);
    Optional<Participant> findByMeetIdAndUserId(Long meetId, Long userId);
}
