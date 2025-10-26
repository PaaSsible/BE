package com.paassible.meetservice.livekit;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import org.springframework.stereotype.Service;

// 서비스: 토큰 생성
@Service
public class LivekitTokenService {
    private final String apiKey;
    private final String apiSecret;

    public LivekitTokenService(LivekitProps props) {
        this.apiKey = props.getApi().getKey();
        this.apiSecret = props.getApi().getSecret();
    }

    public String createJoinToken(String room, String identity, String displayName) {
        AccessToken token = new AccessToken(apiKey, apiSecret);
        token.setIdentity(identity);
        if (displayName != null) token.setName(displayName);
        token.addGrants(
                new RoomJoin(true),
                new RoomName(room)

        );
        token.setTtl(10 * 60 * 1000L);
        return token.toJwt();
    }
}
