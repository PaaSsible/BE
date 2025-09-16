package com.paassible.boardservice.shortcut.repository;


import com.paassible.boardservice.shortcut.entity.Shortcut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortcutRepository extends JpaRepository<Shortcut, Long> {

    Optional<Shortcut> findByIdAndBoardId(Long id, Long boardId);

    List<Shortcut> findByBoardId(Long boardId);
}
