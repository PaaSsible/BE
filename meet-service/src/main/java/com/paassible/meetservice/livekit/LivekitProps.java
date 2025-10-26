package com.paassible.meetservice.livekit;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "livekit")
public class LivekitProps {
    private String url;
    private Api api = new Api();
    @Setter
    @Getter
    public static class Api { private String key; private String secret; }

}

