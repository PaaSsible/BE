package com.paassible.boardservice.task.controller;

import com.paassible.boardservice.task.dto.TaskDescriptionRequest;
import com.paassible.boardservice.task.dto.TaskRequest;
import com.paassible.boardservice.task.dto.TaskResponse;
import com.paassible.boardservice.task.dto.TaskStatusRequest;
import com.paassible.boardservice.task.service.TaskManagementService;
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
@RequestMapping("/board/{boardId}")
@Tag(name = "업무 API", description = "업무 목록 조회, 상세 조회, 생성, 수정(업무, 설명, 상태), 삭제")
public class TaskController {

    private final TaskManagementService taskManagementService;

    @PostMapping("/task")
    @Operation(summary = "업무 생성", description = "새로운 업무를 생성합니다.")
    public ResponseEntity<ApiResponse<Void>> createTask(@AuthenticationPrincipal UserJwtDto user,
                                                        @PathVariable Long boardId,
                                                        @RequestBody TaskRequest request) {
        taskManagementService.createTask(user.getUserId(), boardId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED));
    }

    @PatchMapping("/task/{taskId}")
    @Operation(summary = "업무 수정", description = "업무 내용을 수정합니다(제목, 마감일, 담당자).")
    public ResponseEntity<ApiResponse<Void>> updateTask(@AuthenticationPrincipal UserJwtDto user,
                                                        @PathVariable Long boardId,
                                                        @PathVariable Long taskId,
                                                        @RequestBody TaskRequest request) {
        taskManagementService.updateTask(user.getUserId(), boardId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.MODIFIED));
    }

    @PatchMapping("/task/{taskId}/description")
    @Operation(summary = "업무 설명 수정", description = "업무의 상세 설명을 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateTaskDescription(@AuthenticationPrincipal UserJwtDto user,
                                                                   @PathVariable Long boardId,
                                                                   @PathVariable Long taskId,
                                                                    @RequestBody TaskDescriptionRequest request) {
        taskManagementService.updateTaskDescription(user.getUserId(), boardId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.MODIFIED));
    }

    @PatchMapping("/task/{taskId}/status")
    @Operation(summary = "업무 상태 수정", description = "업무의 상태를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateTaskStatus(@AuthenticationPrincipal UserJwtDto user,
                                                              @PathVariable Long boardId,
                                                              @PathVariable Long taskId,
                                                               @RequestBody TaskStatusRequest request) {
        taskManagementService.updateTaskStatus(user.getUserId(), boardId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.MODIFIED));
    }

    @DeleteMapping("/task/{taskId}")
    @Operation(summary = "업무 삭제", description = "업무를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@AuthenticationPrincipal UserJwtDto user,
                                                        @PathVariable Long boardId,
                                                        @PathVariable Long taskId) {
        taskManagementService.deleteTask(user.getUserId(), boardId, taskId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETED));
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "업무 상세 조회", description = "보드에 대한 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@AuthenticationPrincipal UserJwtDto user,
                                                             @PathVariable Long boardId,
                                                             @PathVariable Long taskId) {
        TaskResponse response = taskManagementService.getTask(user.getUserId(), boardId, taskId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @GetMapping("/tasks")
    @Operation(summary = "업무 목록 조회", description = "프로젝트 보드에 할당된 업무 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByBoard(@AuthenticationPrincipal UserJwtDto user,
                                                                           @PathVariable Long boardId) {
        List<TaskResponse> response = taskManagementService.getByBoard(user.getUserId(), boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
