package com.paassible.boardservice.board.repository;

import com.paassible.boardservice.board.entity.UserBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBoardRepository extends JpaRepository<UserBoard, Long> {

    List<UserBoard> findByUserId(Long userId);
    List<UserBoard> findByBoardId(Long boardId);
}
