package com.paassible.meetservice.livekit;

import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.meetservice.livekit.dto.TokenRequest;
import com.paassible.meetservice.livekit.dto.TokenResponse;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.service.MeetValidator;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/livekit")
public class LivekitController {
    private final LivekitTokenService tokenService;
    private final LivekitProps props;
    private final MeetValidator meetValidator;



    @GetMapping("/token")
    public TokenResponse getToken(@RequestBody TokenRequest request,
                          @AuthenticationPrincipal UserJwtDto user) {

        Meet meet = meetValidator.validateMeetOngoing(request.meetId());
        meetValidator.validateUserInBoard(meet.getBoardId(), user.getUserId());

        String room = "meet-"+request.meetId();

        String token = tokenService.createJoinToken(room,String.valueOf(user.getUserId()), request.displayName());

        return new TokenResponse(props.getUrl(), room, token);
    }
}


