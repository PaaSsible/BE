package com.paassible.boardservice.board.repository;

import com.paassible.boardservice.board.entity.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {

    List<BoardMember> findBoardMembersByUserId(Long userId);
    List<BoardMember> findBoardMembersByBoardId(Long boardId);

    Optional<BoardMember> findByUserIdAndBoardId(Long userId, Long boardId);

    boolean existsByUserIdAndBoardId(Long userId, Long boardId);
}
