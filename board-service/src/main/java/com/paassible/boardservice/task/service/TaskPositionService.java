package com.paassible.boardservice.task.service;

import com.paassible.boardservice.task.entity.TaskPosition;
import com.paassible.boardservice.task.repository.TaskPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskPositionService {

    private final TaskPositionRepository taskPositionRepository;

    @Transactional
    public void assignPositions(Long taskId, List<Long> positionIds) {
        if (positionIds == null || positionIds.isEmpty()) {
            return;
        }
        for (Long positionId : positionIds) {
            TaskPosition taskPosition = TaskPosition.builder()
                    .taskId(taskId)
                    .positionId(positionId)
                    .build();
            taskPositionRepository.save(taskPosition);
        }
    }

    @Transactional
    public void reassignPositions(Long taskId, List<Long> positionIds) {
        taskPositionRepository.deleteAll(taskPositionRepository.findByTaskId(taskId));
        assignPositions(taskId, positionIds);
    }

    public List<Long> getPositionIdsByTaskId(Long taskId) {
        return taskPositionRepository.findByTaskId(taskId).stream()
                .map(TaskPosition::getPositionId)
                .toList();
    }

    public void deleteByTaskId(Long taskId) {
        taskPositionRepository.deleteByTaskId(taskId);
    }
}
