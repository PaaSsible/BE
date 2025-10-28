package com.paassible.boardservice.portfolio.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PortfolioAiRequest {
    private Long userId;
    private Long positionId;
    private String title;
    private String summary;
    private String description;
    private String mainCategory;
    private String subCategory;
    private double contribution;
}