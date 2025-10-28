package com.paassible.boardservice.task.controller;

import com.paassible.boardservice.task.dto.*;
import com.paassible.boardservice.task.service.ContributionService;
import com.paassible.boardservice.task.service.TaskManagementService;
import com.paassible.boardservice.task.service.TaskVisualizationService;
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
@RequestMapping("/boards/{boardId}")
@Tag(name = "업무 API", description = "업무 목록 조회, 상세 조회, 생성, 수정(업무, 설명, 상태), 삭제")
public class TaskController {

    private final TaskManagementService taskManagementService;
    private final TaskVisualizationService taskVisualizationService;
    private final ContributionService contributionService;

    @PostMapping("/tasks")
    @Operation(summary = "업무 생성", description = "새로운 업무를 생성합니다.")
    public ResponseEntity<ApiResponse<Void>> createTask(@AuthenticationPrincipal UserJwtDto user,
                                                        @PathVariable Long boardId,
                                                        @RequestBody TaskRequest request) {
        taskManagementService.createTask(user.getUserId(), boardId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED));
    }

    @PatchMapping("/tasks/{taskId}")
    @Operation(summary = "업무 수정", description = "업무 내용을 수정합니다(제목, 마감일, 담당자).")
    public ResponseEntity<ApiResponse<Void>> updateTask(@AuthenticationPrincipal UserJwtDto user,
                                                        @PathVariable Long boardId,
                                                        @PathVariable Long taskId,
                                                        @RequestBody TaskRequest request) {
        taskManagementService.updateTask(user.getUserId(), boardId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.MODIFIED));
    }

    @PatchMapping("/tasks/{taskId}/description")
    @Operation(summary = "업무 설명 수정", description = "업무의 상세 설명을 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateTaskDescription(@AuthenticationPrincipal UserJwtDto user,
                                                                   @PathVariable Long boardId,
                                                                   @PathVariable Long taskId,
                                                                    @RequestBody TaskDescriptionRequest request) {
        taskManagementService.updateTaskDescription(user.getUserId(), boardId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.MODIFIED));
    }

    @PatchMapping("/tasks/{taskId}/status")
    @Operation(summary = "업무 상태 수정", description = "업무의 상태를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateTaskStatus(@AuthenticationPrincipal UserJwtDto user,
                                                              @PathVariable Long boardId,
                                                              @PathVariable Long taskId,
                                                               @RequestBody TaskStatusRequest request) {
        taskManagementService.updateTaskStatus(user.getUserId(), boardId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.MODIFIED));
    }

    @DeleteMapping("/tasks/{taskId}")
    @Operation(summary = "업무 삭제", description = "업무를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@AuthenticationPrincipal UserJwtDto user,
                                                        @PathVariable Long boardId,
                                                        @PathVariable Long taskId) {
        taskManagementService.deleteTask(user.getUserId(), boardId, taskId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETED));
    }

    @GetMapping("/tasks/{taskId}")
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

    @GetMapping("/tasks/visualization")
    @Operation(summary = "업무 상태 시각화", description = "업무 상태를 시각화합니다.")
    public ResponseEntity<ApiResponse<TaskVisualizationResponse>> getTaskVisualization(@AuthenticationPrincipal UserJwtDto user,
                                                                                        @PathVariable Long boardId) {
        TaskVisualizationResponse response = taskVisualizationService.getTaskVisualization(user.getUserId(), boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @GetMapping("/reports/weekly")
    @Operation(summary = "주간 목표 달성률", description = "주간 목표 달성률을 시각화합니다.")
    public ResponseEntity<ApiResponse<WeeklyGoalResponse>> getWeeklyGoalRate(@AuthenticationPrincipal UserJwtDto user,
                                                                                       @PathVariable Long boardId) {
        WeeklyGoalResponse response = taskVisualizationService.getWeeklyGoalRate(user.getUserId(), boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @GetMapping("/contribution")
    @Operation(summary = "기여도", description = "기여도를 시각화합니다.")
    public ResponseEntity<ApiResponse<List<ContributionResponse>>> getUserContribution(
            @PathVariable Long boardId){
        List<ContributionResponse> response = contributionService.getContributions(boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));

    }


}
