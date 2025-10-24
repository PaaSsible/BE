package com.paassible.meetservice.meet.repository;

import com.paassible.meetservice.meet.entity.Participant;
import com.paassible.meetservice.meet.entity.ParticipantStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByMeetIdAndUserId(Long meetId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Participant p WHERE p.meetId = :meetId AND p.userId = :userId")
    Optional<Participant> findByMeetIdAndUserIdWithLock(
            @Param("meetId") Long meetId,
            @Param("userId") Long userId
    );

    @Query("select p.userId from Participant p where p.meetId = :meetId and p.status = 'JOINED'")
    List<Long> findJoinedUserIdsByMeetId(Long meetId);

    List<Participant> findByMeetIdAndStatus (Long meetId, ParticipantStatus status);

    @Query("""
        SELECT p.userId FROM Participant  p
        WHERE p.meetId = :meetId
        AND p.status = 'JOINED'
        AND p.userId != :excludeUserId 
    """)
    List<Long> findActiveUserIdsByMeetId(Long meetId,Long excludeUserId);
}
