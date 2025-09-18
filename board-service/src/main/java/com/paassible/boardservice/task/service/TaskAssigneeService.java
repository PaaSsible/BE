package com.paassible.boardservice.task.service;

import com.paassible.boardservice.task.entity.TaskAssignee;
import com.paassible.boardservice.task.repository.TaskAssigneeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskAssigneeService {

    private final TaskAssigneeRepository taskAssigneeRepository;

    public void assignUsers(Long taskId, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        for (Long userId : userIds) {
            TaskAssignee taskAssignee = TaskAssignee.builder()
                    .taskId(taskId)
                    .userId(userId)
                    .build();
            taskAssigneeRepository.save(taskAssignee);
        }
    }

    public void reassignUsers(Long taskId, List<Long> userIds) {
        taskAssigneeRepository.deleteAll(taskAssigneeRepository.findByTaskId(taskId));
        assignUsers(taskId, userIds);
    }

    public List<Long> getAssigneeIds(Long taskId) {
        return taskAssigneeRepository.findByTaskId(taskId)
                .stream()
                .map(TaskAssignee::getUserId)
                .toList();
    }

    public void deleteByTaskId(Long taskId) {
        taskAssigneeRepository.deleteAll(taskAssigneeRepository.findByTaskId(taskId));
    }
}
