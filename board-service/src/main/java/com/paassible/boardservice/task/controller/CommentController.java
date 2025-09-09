package com.paassible.boardservice.task.controller;

import com.paassible.boardservice.task.dto.CommentRequest;
import com.paassible.boardservice.task.dto.CommentResponse;
import com.paassible.boardservice.task.service.CommentService;
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
@RequiredArgsConstructor
@RequestMapping("/board/{boardId}/task/{taskId}")
@Tag(name = "업무 댓글 API", description = "업무 댓글 조회, 생성, 수정, 삭제")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/comments")
    @Operation(summary = "업무 댓글 조회", description = "업무에 대한 댓글 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getTask(@AuthenticationPrincipal UserJwtDto user,
                                                                      @PathVariable Long boardId,
                                                                      @PathVariable Long taskId) {
        List<CommentResponse> response = commentService.getCommentsByTask(user.getUserId(), boardId, taskId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @PostMapping("/comment")
    @Operation(summary = "업무 댓글 생성", description = "업무에 새로운 댓글을 생성합니다.")
    public ResponseEntity<ApiResponse<Void>> createTask(@AuthenticationPrincipal UserJwtDto user,
                                                        @PathVariable Long boardId,
                                                        @PathVariable Long taskId,
                                                        @RequestBody CommentRequest request) {
        commentService.createComment(user.getUserId(), boardId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED));
    }

    @PatchMapping("/comment/{commentId}")
    @Operation(summary = "업무 댓글 수정", description = "업무의 댓글을 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateTaskStatus(@AuthenticationPrincipal UserJwtDto user,
                                                              @PathVariable Long boardId,
                                                              @PathVariable Long taskId,
                                                              @PathVariable Long commentId,
                                                              @RequestBody CommentRequest request) {
        commentService.updateComment(user.getUserId(), boardId, taskId, commentId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.MODIFIED));
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "업무 댓글 삭제", description = "업무의 댓글을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@AuthenticationPrincipal UserJwtDto user,
                                                        @PathVariable Long boardId,
                                                        @PathVariable Long taskId,
                                                        @PathVariable Long commentId) {
        commentService.deleteComment(user.getUserId(), boardId, taskId, commentId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETED));
    }
}
