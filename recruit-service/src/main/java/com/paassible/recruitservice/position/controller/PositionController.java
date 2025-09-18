package com.paassible.recruitservice.position.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.recruitservice.position.dto.PositionResponse;
import com.paassible.recruitservice.position.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "포지션 API", description = "포지션 조회")
public class PositionController {

    private final PositionService positionService;

    @Operation(summary = "포지션 조회", description = "")
    @GetMapping("/recruits/positions")
    public ApiResponse<List<PositionResponse>> getAllPositions() {
        List<PositionResponse> response =  positionService.getAllPositions();
        return ApiResponse.success(SuccessCode.OK, response);
    }

    @PostMapping("/recruits/internal/position")
    public ResponseEntity<String> getPositionName(@RequestParam("positionId") Long positionId) {
        String positionName = positionService.getPositionNameById(positionId);
        return ResponseEntity.ok(positionName);
    }


}
