package com.paassible.chatservice.chat.repository;

import com.paassible.chatservice.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    @Query("SELECT m FROM ChatMessage m " +
            "WHERE m.roomId = :roomId " +
            "AND m.createdAt < :createdAt " +
            "ORDER BY m.createdAt DESC")
    List<ChatMessage> findMessagesBefore(
            @Param("roomId") Long roomId,
            @Param("createdAt") LocalDateTime createdAt,
            Pageable pageable
    );
}
