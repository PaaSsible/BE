package com.paassible.meetservice.meet.repository;

import com.paassible.meetservice.meet.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
