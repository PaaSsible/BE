package com.paassible.chatservice.chat.repository;

import com.paassible.chatservice.chat.entity.ChatRoom;
import com.paassible.chatservice.chat.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByBoardIdAndType(Long boardId, RoomType type);

    Optional<ChatRoom> findByBoardId(Long boardId);

    @Query("""
    SELECT r FROM ChatRoom r
    JOIN RoomParticipant p1 ON p1.roomId = r.id AND p1.userId = :userAId
    JOIN RoomParticipant p2 ON p2.roomId = r.id AND p2.userId = :userBId
    WHERE r.type = 'DIRECT'
""")
    Optional<ChatRoom> findDirectRoom(Long userAId, Long userBId);
}