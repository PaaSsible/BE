package com.paassible.userservice.file.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "nhncloud.s3")
public class S3Properties {
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String region;
    private String bucket;
    private String tenantId;
}
