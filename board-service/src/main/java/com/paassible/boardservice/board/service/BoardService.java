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
    public void updateBoard(Long userId, Long boardId, BoardRequest boardRequest) {
        // 유저가 보드에 속하는지 검증하거나 보드 소유자만 변경할 수 있게 한다면 그걸 검증
        // validateUserRole(boardId, userId, Role.OWNER/ADMIN)
        Board board = getBoard(boardId);

        board.updateBoard(
                boardRequest.getName(),
                boardRequest.getContent(),
                boardRequest.getActivityType(),
                boardRequest.getDetailType()
        );
    }

    public void deleteBoard(Long userId, Long boardId) {
        // 누가 삭제하게 할것인가(보드 소유자만?멤버면 가능?)
        // 멤버 입장에서 보드 삭제는 보드 나가기로 하기?
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
