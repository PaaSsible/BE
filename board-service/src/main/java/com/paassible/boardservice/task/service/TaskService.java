package com.paassible.boardservice.task.service;

import com.paassible.boardservice.board.exception.BoardException;
import com.paassible.boardservice.task.dto.TaskDescriptionRequest;
import com.paassible.boardservice.task.dto.TaskRequest;
import com.paassible.boardservice.task.dto.TaskStatusRequest;
import com.paassible.boardservice.task.entity.Task;
import com.paassible.boardservice.task.entity.enums.TaskStatus;
import com.paassible.boardservice.task.exception.TaskException;
import com.paassible.boardservice.task.repository.TaskRepository;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task getTask(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new BoardException(ErrorCode.TASK_NOT_FOUND));
    }

    public Task createTask(Long boardId, TaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .dueDate(request.getDueDate())
                .status(TaskStatus.PENDING)
                .boardId(boardId).build();
        return taskRepository.save(task);
    }

    public Task updateTask(Long boardId, Long taskId, TaskRequest request) {
        Task task = getTask(taskId);

        if (!task.getBoardId().equals(boardId)) {
            throw new TaskException(ErrorCode.BOARD_TASK_NOT_FOUND);
        }

        task.updateTask(request.getTitle(), request.getDueDate());
        return task;
    }

    public void updateTaskDescription(Long boardId, Long taskId, TaskDescriptionRequest request) {
        Task task = getTask(taskId);

        if (!task.getBoardId().equals(boardId)) {
            throw new TaskException(ErrorCode.BOARD_TASK_NOT_FOUND);
        }

        task.updateTaskDescription(request.getDescription());
    }

    public void updateTaskStatus(Long boardId, Long taskId, TaskStatusRequest request) {
        Task task = getTask(taskId);

        if (!task.getBoardId().equals(boardId)) {
            throw new TaskException(ErrorCode.BOARD_TASK_NOT_FOUND);
        }

        task.updateStatus(request.getStatus());
    }

    public void deleteTask(Long boardId, Long taskId) {
        Task task = getTask(taskId);

        if (!task.getBoardId().equals(boardId)) {
            throw new TaskException(ErrorCode.BOARD_TASK_NOT_FOUND);
        }

        taskRepository.deleteById(taskId);
    }

    public List<Task> getTasksByBoard(Long boardId) {
        return taskRepository.findByBoardId(boardId);
    }
}
