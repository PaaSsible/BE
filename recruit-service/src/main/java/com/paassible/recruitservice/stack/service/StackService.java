package com.paassible.recruitservice.stack.service;

import com.paassible.recruitservice.stack.dto.StackResponse;
import com.paassible.recruitservice.stack.repositoty.StackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
