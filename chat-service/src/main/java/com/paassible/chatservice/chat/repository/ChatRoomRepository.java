package com.paassible.chatservice.chat.repository;

import com.paassible.chatservice.chat.entity.ChatRoom;
import com.paassible.chatservice.chat.entity.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByBoardIdAndType(Long boardId, RoomType type);

    Optional<ChatRoom> findByIdAndBoardId(Long roomId, Long boardId);

    @Query("SELECT r.id FROM ChatRoom r WHERE r.boardId = :boardId AND r.type = 'GROUP'")
    Optional<Long> findGroupRoomIdByBoardId(@Param("boardId") Long boardId);
}