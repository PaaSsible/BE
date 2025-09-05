package com.paassible.boardservice.board.service;

import com.paassible.boardservice.board.dto.BoardRequest;
import com.paassible.boardservice.board.entity.Board;
import com.paassible.boardservice.board.entity.enums.BoardStatus;
import com.paassible.boardservice.board.exception.BoardException;
import com.paassible.boardservice.board.repository.BoardRepository;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .content(boardRequest.getContent())
                .activityType(boardRequest.getActivityType())
                .detailType(boardRequest.getDetailType())
                .status(BoardStatus.ONGOING)
                .build();

        return boardRepository.save(board);
    }

    @Transactional
    public void updateBoard(Long boardId, BoardRequest boardRequest) {
        Board board = getBoard(boardId);

        board.updateBoard(
                boardRequest.getName(),
                boardRequest.getContent(),
                boardRequest.getActivityType(),
                boardRequest.getDetailType()
        );
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

    public void validateBoard(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new BoardException(ErrorCode.BOARD_NOT_FOUND);
        }
    }
}
