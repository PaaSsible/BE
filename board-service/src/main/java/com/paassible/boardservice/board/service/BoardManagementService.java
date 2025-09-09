package com.paassible.boardservice.board.service;


import com.paassible.boardservice.board.entity.enums.ProjectRole;
import com.paassible.boardservice.client.ChatClient;
import com.paassible.boardservice.client.UserClient;
import com.paassible.boardservice.client.UserResponse;
import com.paassible.boardservice.board.dto.BoardMemberResponse;
import com.paassible.boardservice.board.dto.BoardRequest;
import com.paassible.boardservice.board.dto.BoardResponse;
import com.paassible.boardservice.board.entity.Board;
import com.paassible.boardservice.board.entity.BoardMember;
import com.paassible.boardservice.board.exception.BoardException;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardManagementService {

    private final BoardService boardService;
    private final BoardMemberService boardMemberService;
    private final UserClient userClient;
    private final ChatClient chatClient;

    // 보드 생성(생성한 사람을 owner로 설정)
    @Transactional
    public void createBoardWithOwner(Long userId, BoardRequest boardRequest) {
        Board board = boardService.createBoard(boardRequest);
        boardMemberService.registerOwner(userId, board.getId());

        //chatClient.createBoardChatRoom(userId, board.getId());
    }

    @Transactional
    public void updateBoard(Long userId, Long boardId, BoardRequest boardRequest) {
        System.out.println("보드 수정 들어옴");
        boardMemberService.validateUserInBoard(userId, boardId);
        System.out.println("유저 검증함");

        BoardMember boardMember = boardMemberService.getBoardMemberByUser(userId);
        if(boardMember.getRole() != ProjectRole.OWNER) {
            throw new BoardException(ErrorCode.BOARD_UPDATE_OWNER);
        }
        Board board = boardService.updateBoard(boardId, boardRequest);
        boardMemberService.registerOwner(userId, board.getId());

    }

    // 보드 수락 시 해당 보드에 참여자 추가
    @Transactional
    public void addUserToBoard(Long userId, Long boardId) {
        if (!boardService.exists(boardId)) {
            throw new BoardException(ErrorCode.BOARD_NOT_FOUND);
        }
        boardMemberService.assignUserToBoard(userId, boardId);
        //chatClient.addParticipant(userId, boardId);
    }

    // 특정 보드의 멤버 목록 조회
    public List<BoardMemberResponse> getUsersByBoard(Long userId, Long boardId) {
        boardMemberService.validateUserInBoard(boardId, userId);

        List<BoardMember> userBoards = boardMemberService.getBoardMembersByBoard(boardId);

        return userBoards.stream()
                .map(ub -> {
                    UserResponse user = userClient.getUser(ub.getUserId());
                    return BoardMemberResponse.from(user, ub);
                })
                .toList();
    }

    // 유저의 보드 목록 조회
    public List<BoardResponse> getBoardsByUser(Long userId) {
        List<BoardMember> boardMembers = boardMemberService.getBoardMembersByUser(userId);

        return boardMembers.stream()
                .map(ub -> {
                    Board board = boardService.getBoard(ub.getBoardId());
                    BoardMember adminUserBoard = boardMemberService.getBoardMembersByBoard(board.getId()).stream()
                            .filter(u -> u.getRole() == ProjectRole.OWNER)
                            .findFirst()
                            .orElseThrow(() -> new BoardException(ErrorCode.OWNER_NOT_FOUND));

                    UserResponse owner = userClient.getUser(adminUserBoard.getUserId());
                    return BoardResponse.from(board, owner);
                })
                .toList();
    }
}