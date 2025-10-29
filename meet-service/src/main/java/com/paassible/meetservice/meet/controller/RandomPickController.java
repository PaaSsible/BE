package com.paassible.meetservice.meet.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.meetservice.livekit.LivekitProps;
import com.paassible.meetservice.meet.service.RandomSpeakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meets")
public class RandomPickController {

    private final RandomSpeakerService randomSpeakerService;

    @PostMapping("/{meetId}/random-pick")
    public ResponseEntity<ApiResponse<Optional<Long>>> randomSpeakerPick (@PathVariable Long meetId,
                                  @AuthenticationPrincipal UserJwtDto user) {

        Optional<Long> response = randomSpeakerService.pickAndBroadcast(meetId, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED, response));
    }
}

