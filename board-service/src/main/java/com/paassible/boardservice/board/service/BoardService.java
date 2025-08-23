package com.paassible.boardservice.board.service;

import com.paassible.boardservice.board.dto.BoardRequest;
import com.paassible.boardservice.board.entity.Board;
import com.paassible.boardservice.board.entity.BoardStatus;
import com.paassible.boardservice.board.exception.BoardException;
import com.paassible.boardservice.board.repository.BoardRepository;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public Board getBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
    }

    public Board createBoard(BoardRequest boardRequest) {

        Board board = Board.builder()
                .name(boardRequest.getName())
                .activityType(boardRequest.getActivityType())
                .detailType(boardRequest.getDetailType())
                .status(BoardStatus.IN_PROGRESS)
                .build();

        boardRepository.save(board);

        return board;
    }

    public void deleteBoard(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new BoardException(ErrorCode.BOARD_NOT_FOUND);
        }
        boardRepository.deleteById(boardId);
    }

    public boolean exists(Long boardId) {
        return boardRepository.existsById(boardId);
    }
}
