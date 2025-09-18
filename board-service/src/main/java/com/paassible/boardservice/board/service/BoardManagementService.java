package com.paassible.boardservice.board.service;


import com.paassible.boardservice.board.dto.BoardEntryResponse;
import com.paassible.boardservice.board.entity.enums.BoardStatus;
import com.paassible.boardservice.board.entity.enums.MemberStatus;
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

    @Transactional
    public void createBoardWithOwner(Long userId, BoardRequest boardRequest) {
        Board board = boardService.createBoard(boardRequest);
        boardMemberService.registerOwner(userId, board.getId());

        //chatClient.createBoardChatRoom(userId, board.getId());
    }

    @Transactional
    public void updateBoard(Long userId, Long boardId, BoardRequest boardRequest) {
        boardMemberService.validateUserInBoard(userId, boardId);

        BoardMember boardMember = boardMemberService.getBoardMember(userId, boardId);
        if(boardMember.getRole() != ProjectRole.OWNER) {
            throw new BoardException(ErrorCode.BOARD_UPDATE_OWNER);
        }
        Board board = boardService.updateBoard(boardId, boardRequest);
        boardMemberService.registerOwner(userId, board.getId());
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        boardMemberService.validateUserInBoard(userId, boardId);

        BoardMember boardMember = boardMemberService.getBoardMember(userId, boardId);
        if(boardMember.getRole() != ProjectRole.OWNER) {
            throw new BoardException(ErrorCode.BOARD_UPDATE_OWNER);
        }
        boardMemberService.deleteBoardMembers(boardId);
        boardService.deleteBoard(boardId);
    }

    public BoardEntryResponse enterBoard(Long userId, Long boardId) {
        BoardMember boardMember = boardMemberService.getBoardMember(userId, boardId);
        return new BoardEntryResponse(boardId, boardMember.getPositionId());
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

    public List<BoardMemberResponse> getUsersByBoard(Long userId, Long boardId) {
        boardMemberService.validateUserInBoard(boardId, userId);

        List<BoardMember> userBoards = boardMemberService.getBoardMembersByBoard(boardId);

        return userBoards.stream()
                .map(ub -> {
                    String userName;
                    String profileImageUrl;
                    if (ub.getStatus() == MemberStatus.INACTIVE) {
                        userName = "알 수 없음";
                        profileImageUrl = "default";
                    } else {
                        UserResponse user = userClient.getUser(ub.getUserId());
                        userName = user.getNickname();
                        profileImageUrl = user.getProfileImageUrl();
                    }
                    return BoardMemberResponse.from(ub.getUserId(), userName, profileImageUrl, ub);
                })
                .toList();
    }

    public List<BoardResponse> getBoardsByUser(Long userId, BoardStatus status, String keyword) {
        List<BoardMember> boardMembers = boardMemberService.getBoardMembersByUser(userId);

        return boardMembers.stream()
                .filter(ub -> ub.getStatus() == MemberStatus.ACTIVE)
                .map(ub -> {
                    Board board = boardService.getBoard(ub.getBoardId());
                    BoardMember adminUserBoard = boardMemberService.getBoardMembersByBoard(board.getId()).stream()
                            .filter(u -> u.getRole() == ProjectRole.OWNER)
                            .findFirst()
                            .orElseThrow(() -> new BoardException(ErrorCode.OWNER_NOT_FOUND));

                    UserResponse owner = userClient.getUser(adminUserBoard.getUserId());
                    return BoardResponse.from(board, owner);
                })
                .filter(response -> status == null
                        || response.getStatus().equals(status.name()))
                .filter(response -> keyword == null
                        || response.getName().toLowerCase().contains(keyword.toLowerCase())
                        || response.getContent().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
}