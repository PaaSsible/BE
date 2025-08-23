package com.paassible.boardservice.board.service;

import com.paassible.boardservice.board.entity.ProjectRole;
import com.paassible.boardservice.board.entity.UserBoard;
import com.paassible.boardservice.board.repository.UserBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBoardService {

    private final UserBoardRepository userBoardRepository;

    public void registerOwner(Long userId, Long boardId) {
        UserBoard userBoard = UserBoard.builder()
                .userId(userId)
                .boardId(boardId)
                .role(ProjectRole.OWNER)
                .build();
        userBoardRepository.save(userBoard);
    }

    public void assignUserToBoard(Long userId, Long boardId) {
        UserBoard userBoard = UserBoard.builder()
                .userId(userId)
                .boardId(boardId)
                .role(ProjectRole.MEMBER)
                .build();
        userBoardRepository.save(userBoard);
    }

    public List<UserBoard> getUserBoardsByBoard(Long boardId) {
        return userBoardRepository.findByBoardId(boardId)
                .stream()
                .toList();
    }

    public List<UserBoard> getUserBoardsByUser(Long userId) {
        return userBoardRepository.findByUserId(userId)
                .stream()
                .toList();
    }
}
