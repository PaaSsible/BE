package com.paassible.recruitservice.stack.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.recruitservice.position.entity.Position;
import com.paassible.recruitservice.stack.dto.StackResponse;
import com.paassible.recruitservice.stack.entity.Stack;
import com.paassible.recruitservice.stack.repositoty.StackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StackService {
    private final StackRepository stackRepository;

    public List<StackResponse> getAllStacks() {
        return stackRepository.findAll().stream()
                .map(s->new StackResponse(s.getId(), s.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getStackNames(List<Long> stackIds) {
        return stackRepository.findAllByIdIn(stackIds)
                .stream()
                .map(Stack::getName)
                .toList();
    }
}
