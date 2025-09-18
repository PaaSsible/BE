package com.paassible.recruitservice.position.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.recruitservice.position.dto.PositionResponse;
import com.paassible.recruitservice.position.entity.Position;
import com.paassible.recruitservice.position.repositoty.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public String getPositionNameById(Long positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new CustomException(ErrorCode.POSITION_NOT_FOUND));
        return position.getName();
    }
}
