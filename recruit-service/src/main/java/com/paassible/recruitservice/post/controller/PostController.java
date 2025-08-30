package com.paassible.recruitservice.post.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.recruitservice.post.dto.*;
import com.paassible.recruitservice.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruit")
@RequiredArgsConstructor
@Tag(name = "팀원 모집 게시판 API", description = "팀원 모집 게시판 조회, 상세 조회, 생성, 삭제")
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    @Operation(summary = "게시판 글 상세조회")
    public ApiResponse<PostDetailResponse> getPost(@PathVariable Long postId) {
        PostDetailResponse postDetailResponse = postService.getPostDetail(postId);
        return ApiResponse.success(SuccessCode.OK, postDetailResponse);
    }

    @PostMapping
    @Operation(summary = "게시판 글 작성", description = "팀원 모집 게시판에 글을 작성합니다.")
    public ApiResponse<PostCreateResponse> createPost(
            @AuthenticationPrincipal UserJwtDto user, @RequestBody  @Valid PostCreateRequest request) {
        PostCreateResponse response = postService.createPost(request, user.getUserId());
        return ApiResponse.success(SuccessCode.CREATED, response);
    }


    @PutMapping("/{postId}")
    @Operation(summary = "게시판 글 수정")
    public ApiResponse<PostUpdateResponse> updatePost(
            @RequestParam Long postId, @RequestBody  @Valid PostUpdateRequest request, @AuthenticationPrincipal UserJwtDto user
    ){
        PostUpdateResponse response = postService.updatePost(postId,request, user.getUserId());
        return ApiResponse.success(SuccessCode.OK,response);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시판 글 삭제")
    public ApiResponse<Void> deletePost(
            @RequestParam Long postId,@AuthenticationPrincipal UserJwtDto user
    ){
        postService.deletePost(postId,user.getUserId());
        return ApiResponse.success(SuccessCode.OK);
    }



}
