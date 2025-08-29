package com.paassible.boardservice.board.controller;

import com.paassible.boardservice.board.dto.BoardMemberResponse;
import com.paassible.boardservice.board.dto.BoardRequest;
import com.paassible.boardservice.board.dto.UserBoardResponse;
import com.paassible.boardservice.board.service.BoardManagementService;
import com.paassible.boardservice.board.service.BoardService;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@Tag(name = "보드 API", description = "보드 목록 조회, 상세 조회, 생성, 삭제")
public class BoardController {

    private final BoardService boardService;
    private final BoardManagementService boardManagementService;

    @PostMapping
    @Operation(summary = "보드 생성", description = "새로운 프로젝트 보드를 생성합니다.")
    public ResponseEntity<ApiResponse<Void>> createBoard(@AuthenticationPrincipal UserJwtDto user,
                                                         @RequestBody BoardRequest boardRequest) {
        boardManagementService.createBoardWithOwner(user.getUserId(), boardRequest);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED));
    }

    @DeleteMapping("/{boardId}")
    @Operation(summary = "보드 삭제", description = "프로젝트 보드를 삭제합니다.")
    // 혼자 만든 프로젝트만 삭제 가능하도록 만들어야할지?
    // 함께한 프로젝트면 자기만 방 나가기처럼?

    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETED));
    }

    @GetMapping("/{boardId}")
    @Operation(summary = "특정 보드의 유저 목록 조회", description = "해당 보드에 참여한 유저들의 목록을 조회한다.")
    public ResponseEntity<ApiResponse<List<BoardMemberResponse>>> getUsersByBoard(@PathVariable Long boardId) {
        List<BoardMemberResponse> response = boardManagementService.getUsersByBoard(boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @GetMapping
    @Operation(summary = "특정 유저의 보드 목록 조회", description = "유저가 참여하는 보드 목록을 조회한다.")
    public ResponseEntity<ApiResponse<List<UserBoardResponse>>> getBoardsByUser(@AuthenticationPrincipal UserJwtDto user) {
        List<UserBoardResponse> response = boardManagementService.getBoardsByUser(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
