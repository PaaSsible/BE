package com.paassible.boardservice.task.service;

import com.paassible.boardservice.board.service.BoardMemberService;
import com.paassible.boardservice.task.dto.TaskVisualizationResponse;
import com.paassible.boardservice.task.entity.enums.TaskStatus;
import com.paassible.boardservice.task.repository.TaskCountProjection;
import com.paassible.boardservice.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskVisualizationService {

    private final TaskRepository taskRepository;
    private final BoardMemberService boardMemberService;

    public TaskVisualizationResponse getTaskVisualization(Long userId, Long boardId) {
        boardMemberService.validateUserInBoard(userId, boardId);

        List<TaskCountProjection> taskCounts = taskRepository.countTasksByStatus();

        Map<String, Integer> countMap = taskCounts.stream()
                .collect(Collectors.toMap(TaskCountProjection::getStatus, TaskCountProjection::getCount));

        List<TaskVisualizationResponse.TaskDto> tasks = Arrays.stream(TaskStatus.values())
                .map(status -> new TaskVisualizationResponse.TaskDto(
                        status.name(),
                        countMap.getOrDefault(status.name(), 0)
                ))
                .toList();

        int total = tasks.stream()
                .mapToInt(TaskVisualizationResponse.TaskDto::getCount)
                .sum();

        int completedCount = tasks.stream()
                .filter(t -> TaskStatus.COMPLETED.name().equals(t.getStatus()))
                .mapToInt(TaskVisualizationResponse.TaskDto::getCount)
                .sum();

        int completedRate = total == 0 ? 0 : (completedCount * 100) / total;

        return TaskVisualizationResponse.from(completedRate, total, tasks);
    }
}
