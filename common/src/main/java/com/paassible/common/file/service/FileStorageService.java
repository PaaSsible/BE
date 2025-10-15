package com.paassible.common.file.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    public String save(MultipartFile file) {
        return "url";
    }
}
