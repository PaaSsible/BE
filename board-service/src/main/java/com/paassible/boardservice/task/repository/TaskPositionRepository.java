package com.paassible.boardservice.task.repository;

import com.paassible.boardservice.task.entity.TaskPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskPositionRepository extends JpaRepository<TaskPosition, Long> {
    List<TaskPosition> findByTaskId(Long taskId);
}