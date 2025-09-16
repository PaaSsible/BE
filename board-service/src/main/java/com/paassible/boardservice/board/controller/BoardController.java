package com.paassible.boardservice.board.controller;

import com.paassible.boardservice.board.dto.BoardMemberResponse;
import com.paassible.boardservice.board.dto.BoardRequest;
import com.paassible.boardservice.board.dto.BoardResponse;
import com.paassible.boardservice.board.entity.enums.BoardStatus;
import com.paassible.boardservice.board.service.BoardManagementService;
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
@Tag(name = "보드 API", description = "보드 멤버 조회, 목록 조회, 상세 조회, 생성, 수정, 삭제")
public class BoardController {

    private final BoardManagementService boardManagementService;

    @PostMapping
    @Operation(summary = "보드 생성", description = "새로운 프로젝트 보드를 생성합니다.")
    public ResponseEntity<ApiResponse<Void>> createBoard(@AuthenticationPrincipal UserJwtDto user,
                                                         @RequestBody BoardRequest boardRequest) {
        boardManagementService.createBoardWithOwner(user.getUserId(), boardRequest);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED));
    }

    @PutMapping("/{boardId}")
    @Operation(summary = "보드 수정", description = "프로젝트 보드를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateBoard(@AuthenticationPrincipal UserJwtDto user,
                                                         @PathVariable Long boardId,
                                                         @RequestBody BoardRequest boardRequest) {
        boardManagementService.updateBoard(user.getUserId(), boardId, boardRequest);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.MODIFIED));
    }

    @DeleteMapping("/{boardId}")
    @Operation(summary = "보드 삭제", description = "프로젝트 보드를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@AuthenticationPrincipal UserJwtDto user,
                                                         @PathVariable Long boardId) {
        boardManagementService.deleteBoard(user.getUserId(), boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETED));
    }

    @DeleteMapping("/{boardId}/leave")
    @Operation(summary = "보드 탈퇴", description = "프로젝트 보드를 탈퇴합니다.")
    public ResponseEntity<ApiResponse<Void>> leaveBoard(@AuthenticationPrincipal UserJwtDto user,
                                                         @PathVariable Long boardId) {
        boardManagementService.leaveBoard(user.getUserId(), boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETED));
    }

    @GetMapping("/{boardId}")
    @Operation(summary = "보드 멤버 조회", description = "해당 보드에 참여한 유저들의 목록을 조회한다.")
    public ResponseEntity<ApiResponse<List<BoardMemberResponse>>> getUsersByBoard(@AuthenticationPrincipal UserJwtDto user,
                                                                                  @PathVariable Long boardId) {
        List<BoardMemberResponse> response = boardManagementService.getUsersByBoard(user.getUserId(), boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @GetMapping
    @Operation(summary = "보드 목록 조회", description = "유저가 참여하는 보드 목록을 조회한다.")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getBoardsByUser(@AuthenticationPrincipal UserJwtDto user,
                                                                            @RequestParam(required = false) BoardStatus status,
                                                                            @RequestParam(required = false) String keyword) {
        List<BoardResponse> response = boardManagementService.getBoardsByUser(user.getUserId(), status, keyword);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
