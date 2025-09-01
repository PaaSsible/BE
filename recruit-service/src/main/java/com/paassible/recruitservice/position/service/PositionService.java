package com.paassible.recruitservice.position.service;

import com.paassible.recruitservice.position.dto.PositionResponse;
import com.paassible.recruitservice.position.repositoty.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;

    public List<PositionResponse> getAllPositions(){
        return positionRepository.findAll().stream()
                .map(p->new PositionResponse(p.getId(), p.getName()))
                .toList();
    }


}
