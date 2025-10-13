package com.paassible.userservice.user.dto;

import lombok.Getter;

@Getter
public class PortfolioRequest {
    private String title;
    private String description;
    private String fileUrl;
    private String fileName;
    private String fileType;
}
