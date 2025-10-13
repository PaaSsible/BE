package com.paassible.userservice.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    private String description;

    private String fileUrl;

    private String fileName;

    private String fileType;

    public void updateInfo(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void updateFile(String fileUrl, String fileName, String fileType) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileType = fileType;
    }
}
