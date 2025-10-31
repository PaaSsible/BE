package com.paassible.recruitservice.comment.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.recruitservice.comment.dto.CommentCreateRequest;
import com.paassible.recruitservice.comment.dto.CommentListResponse;
import com.paassible.recruitservice.comment.dto.CommentUpdateRequest;
import com.paassible.recruitservice.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruits")
@Tag(name = "팀원 모집 게시판 댓글 API", description = "댓글 생성,조회,수정,삭제")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("{postId}/comments")
    @Operation(summary = "댓글 작성")
    public ApiResponse<Void> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserJwtDto user
    ) {
        commentService.createComment(postId, request, user.getUserId());
        return ApiResponse.success(SuccessCode.OK);
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "댓글 조회")
    public ApiResponse<CommentListResponse> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserJwtDto user) {
       CommentListResponse response = commentService.getComments(postId, user.getUserId());
        return ApiResponse.success(SuccessCode.OK,response);
    }

    @PutMapping("/comments/{commentId}")
    @Operation(summary = "댓글 수정")
    public ApiResponse<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal UserJwtDto user){
        commentService.updateComment(commentId,user.getUserId(),request.content());
        return ApiResponse.success(SuccessCode.MODIFIED);
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "댓글 삭제")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserJwtDto user) {
        commentService.deleteComment(commentId, user.getUserId());
        return ApiResponse.success(SuccessCode.DELETED);
    }
}

