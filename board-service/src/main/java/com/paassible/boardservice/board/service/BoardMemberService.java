package com.paassible.boardservice.board.service;

import com.paassible.boardservice.board.entity.enums.MemberStatus;
import com.paassible.boardservice.board.entity.enums.ProjectRole;
import com.paassible.boardservice.board.entity.BoardMember;
import com.paassible.boardservice.board.exception.BoardException;
import com.paassible.boardservice.board.repository.BoardMemberRepository;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardMemberService {

    private final BoardMemberRepository boardMemberRepository;

    public void registerOwner(Long userId, Long boardId) {
        BoardMember userBoard = BoardMember.builder()
                .userId(userId)
                .boardId(boardId)
                .role(ProjectRole.OWNER)
                .build();
        boardMemberRepository.save(userBoard);
    }

    public void assignUserToBoard(Long userId, Long boardId) {
        BoardMember userBoard = BoardMember.builder()
                .userId(userId)
                .boardId(boardId)
                .role(ProjectRole.MEMBER)
                .build();
        boardMemberRepository.save(userBoard);
    }

    @Transactional
    public void updatePosition(Long userId, Long boardId, Long positionId) {
        validateUserInBoard(userId, boardId);
        BoardMember boardMember = getBoardMember(userId, boardId);
        boardMember.updatePosition(positionId);
    }

    @Transactional
    public void leaveBoard(Long userId, Long boardId) {
        validateUserInBoard(userId, boardId);

        BoardMember boardMember = getBoardMember(userId, boardId);
        boardMember.updateMemberStatus(MemberStatus.INACTIVE);
    }

    public List<BoardMember> getBoardMembersByBoard(Long boardId) {
        return boardMemberRepository.findBoardMembersByBoardId(boardId)
                .stream()
                .toList();
    }

    public List<BoardMember> getBoardMembersByUser(Long userId) {
        return boardMemberRepository.findBoardMembersByUserId(userId)
                .stream()
                .toList();
    }

    public BoardMember getBoardMember(Long userId, Long boardId) {
        return boardMemberRepository.findByUserIdAndBoardId(userId, boardId).orElseThrow(
                () -> new BoardException(ErrorCode.BOARD_USER_NOT_FOUND));
    }

    public void validateUserInBoard(Long userId, Long boardId) {
        if (!boardMemberRepository.existsByUserIdAndBoardId(userId, boardId)){
            throw new BoardException(ErrorCode.BOARD_USER_NOT_FOUND);
        }
    }

    public void deleteBoardMembers(Long boardId) {
        boardMemberRepository.deleteAllByBoardId(boardId);
    }
}
