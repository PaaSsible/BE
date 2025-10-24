package com.paassible.boardservice.board.controller;

import com.paassible.boardservice.board.dto.BoardMemberResponse;
import com.paassible.boardservice.board.service.BoardManagementService;
import com.paassible.boardservice.board.service.BoardService;
import com.paassible.boardservice.board.service.BoardMemberService;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Hidden
@RestController
@RequestMapping("/boards/internal")
@RequiredArgsConstructor
public class BoardInternalController {

    private final BoardService boardService;
    private final BoardMemberService boardMemberService;
    private final BoardManagementService boardManagementService;

    @Hidden
    @GetMapping("/{boardId}/exists")
    public void validateBoard(@PathVariable Long boardId) {
        boardService.validateBoard(boardId);
    }

    @Hidden
    @GetMapping("/{boardId}/user/{userId}/exists")
    public void existUserInBoard(@PathVariable Long boardId, @PathVariable Long userId) {
        boardMemberService.validateUserInBoard(userId, boardId);
    }

    @Hidden
    @PostMapping("/{boardId}/members")
    public void addMember(
            @PathVariable Long boardId, @RequestParam Long userId) {
        boardManagementService.addUserToBoard(userId, boardId);
    }

    @Hidden
    @GetMapping("/{boardId}/members")
    public ResponseEntity<List<BoardMemberResponse>> getUsersByBoard(@PathVariable Long boardId) {
        List<BoardMemberResponse> response = boardManagementService.getUsersInBoard(boardId);
        return ResponseEntity.ok(response);
    }
}