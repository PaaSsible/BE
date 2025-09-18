package com.paassible.recruitservice.stack.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.recruitservice.stack.dto.StackResponse;
import com.paassible.recruitservice.stack.service.StackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "기술 스택 API", description = "기술 스택 조회")
public class StackController {
    private final StackService stackService;

    @GetMapping("/recruits/stacks")
    @Operation(summary = "포지션 조회", description = "")
    public ApiResponse<List<StackResponse>> getAllStacks() {
        List<StackResponse> response = stackService.getAllStacks();
        return ApiResponse.success(SuccessCode.OK, response);
    }
}
