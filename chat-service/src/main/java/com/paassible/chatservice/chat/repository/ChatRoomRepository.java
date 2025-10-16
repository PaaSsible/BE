package com.paassible.chatservice.chat.repository;

import com.paassible.chatservice.chat.entity.ChatRoom;
import com.paassible.chatservice.chat.entity.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByBoardIdAndType(Long boardId, RoomType type);

    Optional<ChatRoom> findByIdAndBoardId(Long roomId, Long boardId);
}