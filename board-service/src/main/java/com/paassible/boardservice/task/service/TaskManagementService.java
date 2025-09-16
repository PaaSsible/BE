package com.paassible.boardservice.task.service;

import com.paassible.boardservice.board.entity.BoardMember;
import com.paassible.boardservice.board.entity.enums.MemberStatus;
import com.paassible.boardservice.board.service.BoardMemberService;
import com.paassible.boardservice.client.UserClient;
import com.paassible.boardservice.task.dto.TaskDescriptionRequest;
import com.paassible.boardservice.task.dto.TaskRequest;
import com.paassible.boardservice.task.dto.TaskResponse;
import com.paassible.boardservice.task.dto.TaskStatusRequest;
import com.paassible.boardservice.task.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskManagementService {

    private final TaskService taskService;
    private final TaskAssigneeService taskAssigneeService;
    private final BoardMemberService boardMemberService;
    private final UserClient userClient;

    @Transactional
    public void createTask(Long userId, Long boardId, TaskRequest request) {
        boardMemberService.validateUserInBoard(boardId, userId);
        Task task = taskService.createTask(boardId, request);
        taskAssigneeService.assignUsers(task.getId(), request.getAssigneeIds());
    }

    @Transactional
    public void updateTask(Long userId, Long boardId, Long taskId, TaskRequest request) {
        boardMemberService.validateUserInBoard(boardId, userId);
        Task task = taskService.updateTask(boardId, taskId, request);
        taskAssigneeService.reassignUsers(task.getId(), request.getAssigneeIds());
    }

    @Transactional
    public void updateTaskDescription(Long userId, Long boardId, Long taskId, TaskDescriptionRequest request) {
        boardMemberService.validateUserInBoard(boardId, userId);
        taskService.updateTaskDescription(boardId, taskId, request);
    }

    @Transactional
    public void updateTaskStatus(Long userId, Long boardId, Long taskId, TaskStatusRequest request) {
        boardMemberService.validateUserInBoard(boardId, userId);
        taskService.updateTaskStatus(boardId, taskId, request);
    }

    @Transactional
    public void deleteTask(Long userId, Long boardId, Long taskId) {
        boardMemberService.validateUserInBoard(boardId, userId);
        taskAssigneeService.deleteByTaskId(taskId);
        taskService.deleteTask(boardId, taskId);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long userId, Long boardId, Long taskId) {
        boardMemberService.validateUserInBoard(boardId, userId);
        Task task = taskService.getTask(taskId);
        return toResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getByBoard(Long userId, Long boardId) {
        boardMemberService.validateUserInBoard(boardId, userId);
        List<Task> tasks = taskService.getTasksByBoard(boardId);
        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }

    private TaskResponse toResponse(Task task) {
        List<TaskResponse.AssigneeDto> assignees = taskAssigneeService.getAssigneeIds(task.getId())
                .stream()
                .map(userId -> {
                    BoardMember boardMember = boardMemberService.getBoardMember(userId, task.getBoardId());

                    String name;
                    if (boardMember.getStatus() == MemberStatus.INACTIVE) {
                        name = "알 수 없음";
                    } else {
                        name = userClient.getUser(userId).getNickname();
                    }

                    return TaskResponse.AssigneeDto.builder()
                            .userId(userId)
                            .name(name)
                            .build();
                })
                .toList();

        return TaskResponse.from(task, assignees);
    }
}
