package com.paassible.userservice.user.dto;

import lombok.Getter;

@Getter
public class PortfolioAiRequest {
    private Long userId;
    private Long positionId;
    private String title;
    private String summary;
    private String description;
    private String mainCategory;
    private String subCategory;
    private int contribution;
}