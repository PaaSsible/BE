package com.paassible.userservice.user.dto;

import com.paassible.userservice.user.entity.Portfolio;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortfolioResponse {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String fileUrl;
    private String fileName;
    private String fileType;

    public static PortfolioResponse from(Portfolio portfolio) {
        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .userId(portfolio.getUserId())
                .title(portfolio.getTitle())
                .description(portfolio.getDescription())
                .fileUrl(portfolio.getFileUrl())
                .fileName(portfolio.getFileName())
                .fileType(portfolio.getFileType())
                .build();
    }
}