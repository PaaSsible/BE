package com.paassible.meetservice.meet.controller;

import com.paassible.meetservice.meet.service.MeetAttendanceService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/meets/internal/attendance")
public class MeetInternalController {

    private final MeetAttendanceService meetAttendanceService;


    @GetMapping("/rate")
    public ResponseEntity<Double> getAttendanceRate(
            @RequestParam Long userId,
            @RequestParam Long boardId
    ) {
        Double response = meetAttendanceService.calculateAttendanceRate(boardId, userId);
        return ResponseEntity.ok(response);
    }
}
