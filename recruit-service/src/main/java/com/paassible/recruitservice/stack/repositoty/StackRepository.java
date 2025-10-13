package com.paassible.recruitservice.stack.repositoty;

import com.paassible.recruitservice.stack.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StackRepository extends JpaRepository<Stack, Long> {
    List<Stack> findAllByIdIn(List<Long> ids);
}