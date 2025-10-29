package com.paassible.userservice.file.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.userservice.file.dto.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;
    private final S3UrlService s3UrlService;

    public String upload(String dirName, MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_FILE_NAME);
        }

        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName.replaceAll("\\s", "_");

        String fileName = dirName + "/" + uniqueFileName;
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return s3UrlService.generateFileUrl(fileName);

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    public void deleteFile(String fileUrl) {
        String originalFileName = s3UrlService.extractKey(fileUrl);
        System.out.println(originalFileName);
        String decodedFileName = URLDecoder.decode(originalFileName, StandardCharsets.UTF_8);

        DeleteObjectRequest deleteObjectRequest =
                DeleteObjectRequest.builder().bucket(s3Properties.getBucket()).key(decodedFileName).build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    public String extractFileName(String url) {
        if (url == null || !url.contains("/")) return null;

        String lastSegment = url.substring(url.lastIndexOf('/') + 1);

        int underscoreIndex = lastSegment.indexOf('_');
        String nameWithExt = (underscoreIndex != -1)
                ? lastSegment.substring(underscoreIndex + 1)
                : lastSegment;

        int dotIndex = nameWithExt.lastIndexOf('.');
        return (dotIndex != -1)
                ? nameWithExt.substring(0, dotIndex)
                : nameWithExt;
    }
}
