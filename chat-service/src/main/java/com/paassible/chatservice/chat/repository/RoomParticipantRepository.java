package com.paassible.chatservice.chat.repository;

import com.paassible.chatservice.chat.entity.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    List<RoomParticipant> findByUserId(Long userId);

    Optional<RoomParticipant> findByRoomIdAndUserId(Long roomId, Long userId);

    @Query("select rp from RoomParticipant rp where rp.roomId = :roomId")
    List<RoomParticipant> findAllByRoomId(@Param("roomId") Long roomId);

    @Query("select count(rp) from RoomParticipant rp " +
            "where rp.roomId = :roomId and rp.lastReadMessageId >= :messageId")
    long countReaders(@Param("roomId") Long roomId,
                      @Param("messageId") Long messageId);
}
