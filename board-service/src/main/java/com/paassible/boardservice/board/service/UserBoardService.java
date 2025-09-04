package com.paassible.boardservice.board.service;

import com.paassible.boardservice.board.entity.enums.ProjectRole;
import com.paassible.boardservice.board.entity.UserBoard;
import com.paassible.boardservice.board.exception.BoardException;
import com.paassible.boardservice.board.repository.UserBoardRepository;
import com.paassible.common.response.ErrorCode;
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

    public void validateUserInBoard(Long boardId, Long userId) {
        if (! userBoardRepository.existsByUserIdAndBoardId(userId, boardId)){
            throw new BoardException(ErrorCode.BOARD_USER_NOT_FOUND);
        }
    }
}
