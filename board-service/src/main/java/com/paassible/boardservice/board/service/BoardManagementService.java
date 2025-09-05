package com.paassible.boardservice.board.service;


import com.paassible.boardservice.client.ChatClient;
import com.paassible.boardservice.client.UserClient;
import com.paassible.boardservice.client.UserResponse;
import com.paassible.boardservice.board.dto.BoardMemberResponse;
import com.paassible.boardservice.board.dto.BoardRequest;
import com.paassible.boardservice.board.dto.UserBoardResponse;
import com.paassible.boardservice.board.entity.Board;
import com.paassible.boardservice.board.entity.UserBoard;
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
    private final UserBoardService userBoardService;
    private final UserClient userClient;
    private final ChatClient chatClient;

    // 보드 생성(생성한 사람을 owner로 설정)
    @Transactional
    public void createBoardWithOwner(Long userId, BoardRequest boardRequest) {
        Board board = boardService.createBoard(boardRequest);
        userBoardService.registerOwner(userId, board.getId());

        //chatClient.createBoardChatRoom(userId, board.getId());
    }

    // 보드 수락 시 해당 보드에 참여자 추가
    @Transactional
    public void addUserToBoard(Long userId, Long boardId) {
        if (!boardService.exists(boardId)) {
            throw new BoardException(ErrorCode.BOARD_NOT_FOUND);
        }
        userBoardService.assignUserToBoard(userId, boardId);
        //chatClient.addParticipant(userId, boardId);
    }

    // 특정 보드의 유저 목록 조회
    public List<BoardMemberResponse> getUsersByBoard(Long boardId) {
        List<UserBoard> userBoards = userBoardService.getUserBoardsByBoard(boardId);

        return userBoards.stream()
                .map(ub -> {
                    UserResponse user = userClient.getUser(ub.getUserId());
                    System.out.println(user.getId());
                    return BoardMemberResponse.builder()
                            .userId(user.getId())
                            .userName(user.getNickname())
                            .role(ub.getRole().name())
                            .build();
                })
                .toList();
    }

    public List<UserBoardResponse> getBoardsByUser(Long userId) {
        List<UserBoard> userBoards = userBoardService.getUserBoardsByUser(userId);

        return userBoards.stream()
                .map(ub -> {
                    Board board = boardService.getBoard(ub.getBoardId());
                    return UserBoardResponse.builder()
                            .boardId(board.getId())
                            .name(board.getName())
                            .activityType(board.getActivityType())
                            .detailType(board.getDetailType())
                            .status(board.getStatus().name())
                            .role(ub.getRole().name())
                            .build();
                })
                .toList();
    }
}