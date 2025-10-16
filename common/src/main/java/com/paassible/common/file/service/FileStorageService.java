package com.paassible.common.file.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.file.dto.S3Properties;
import com.paassible.common.response.ErrorCode;
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
public class FileStorageService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

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

            return s3Client.utilities()
                    .getUrl(builder -> builder.bucket(s3Properties.getBucket()).key(fileName))
                    .toExternalForm();

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    public void deleteFile(String fileName) {
        String splitFilename = ".com/";
        String originalFileName =
                fileName.substring(fileName.lastIndexOf(splitFilename) + splitFilename.length());

        String decodedFileName = URLDecoder.decode(originalFileName, StandardCharsets.UTF_8);

        DeleteObjectRequest deleteObjectRequest =
                DeleteObjectRequest.builder().bucket(s3Properties.getBucket()).key(decodedFileName).build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    public String updateFile(MultipartFile newFile, String oldFileName, String dirName)
            throws IOException {
        if (oldFileName != null && !oldFileName.isEmpty()) {
            deleteFile(oldFileName);
        }

        return upload(dirName, newFile);
    }
}
