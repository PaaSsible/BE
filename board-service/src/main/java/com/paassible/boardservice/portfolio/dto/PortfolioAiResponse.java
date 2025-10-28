package com.paassible.boardservice.portfolio.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PortfolioAiResponse {
    private Long userId;
    private String positionName;
    private String title;
    private String summary;
    private String description;
    private String mainCategory;
    private String subCategory;
    private double contribution;

    public static PortfolioAiResponse from(PortfolioAiRequest request, String positionName) {
        return PortfolioAiResponse.builder()
                .userId(request.getUserId())
                .positionName(positionName)
                .title(request.getTitle())
                .summary(request.getSummary())
                .description(request.getDescription())
                .mainCategory(request.getMainCategory())
                .subCategory(request.getSubCategory())
                .contribution(request.getContribution())
                .build();
    }
}