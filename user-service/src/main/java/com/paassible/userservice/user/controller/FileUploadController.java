package com.paassible.userservice.user.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.userservice.file.service.ObjectStorageService;
import com.paassible.userservice.user.dto.UploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class FileUploadController {

    private final ObjectStorageService objectStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadResponse>> uploadFile(@RequestPart("file") MultipartFile file) {
        UploadResponse response = new UploadResponse(objectStorageService.upload("user", file));
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
