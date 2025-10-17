package com.paassible.userservice.file.service;

import com.paassible.userservice.file.dto.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3UrlService {

    private final S3Properties s3Properties;

    public String generateFileUrl(String fileName) {
        String endpoint = s3Properties.getEndpoint();
        String tenantId = s3Properties.getTenantId();
        String bucket = s3Properties.getBucket();

        return String.format("%s/v1/AUTH_%s/%s/%s", endpoint, tenantId, bucket, fileName);
    }

    public String extractKey(String fileUrl) {
        String storagePathPrefix = "AUTH_" + s3Properties.getTenantId() + "/" + s3Properties.getBucket() + "/";
        return fileUrl.substring(fileUrl.lastIndexOf(storagePathPrefix) + storagePathPrefix.length());
    }

}
