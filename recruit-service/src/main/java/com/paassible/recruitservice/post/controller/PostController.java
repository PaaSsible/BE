package com.paassible.recruitservice.post.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.recruitservice.post.dto.PostCreateRequest;
import com.paassible.recruitservice.post.dto.PostCreateResponse;
import com.paassible.recruitservice.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruit")
@RequiredArgsConstructor
@Tag(name = "팀원 모집 게시판 API", description = "팀원 모집 게시판 조회, 상세 조회, 생성, 삭제")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "게시판 글 작성", description = "팀원 모집 게시판에 글을 작성합니다.")
    public ApiResponse<PostCreateResponse> createPost(
            @RequestParam Long writerId, @RequestBody  @Valid PostCreateRequest request) {
        PostCreateResponse response = postService.createPost(request,writerId);
        return ApiResponse.success(SuccessCode.CREATED, response);
    }

}
