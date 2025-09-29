package com.paassible.boardservice.board.controller;

import com.paassible.boardservice.board.service.BoardService;
import com.paassible.boardservice.board.service.BoardMemberService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Hidden
@RestController
@RequestMapping("/boards/internal")
@RequiredArgsConstructor
public class BoardInternalController {

    private final BoardService boardService;
    private final BoardMemberService boardMemberService;

    @Hidden
    @GetMapping("/{boardId}/exists")
    public void validateBoard(@PathVariable Long boardId) {
        boardService.validateBoard(boardId);
    }

    @Hidden
    @GetMapping("/{boardId}/user/{userId}/exists")
    public void existUserInBoard(@PathVariable Long boardId, @PathVariable Long userId) {
        boardMemberService.validateUserInBoard(boardId, userId);
    }
}