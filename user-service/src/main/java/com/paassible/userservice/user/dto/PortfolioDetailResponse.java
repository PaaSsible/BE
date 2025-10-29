package com.paassible.userservice.user.dto;

import com.paassible.userservice.user.entity.Portfolio;
import com.paassible.userservice.user.entity.enums.MainCategory;
import com.paassible.userservice.user.entity.enums.SubCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PortfolioDetailResponse {
    private Long id;
    private Long userId;
    private String positionName;
    private String title;
    private String summary;
    private String description;
    private MainCategory mainCategory;
    private SubCategory subCategory;
    private int contribution;
    private String imageUrl;
    private String imageName;
    private boolean generatedByAi;
    private LocalDateTime createdAt;

    public static PortfolioDetailResponse from(Portfolio portfolio, String positionName, String imageName) {
        return PortfolioDetailResponse.builder()
                .id(portfolio.getId())
                .userId(portfolio.getUserId())
                .positionName(positionName)
                .title(portfolio.getTitle())
                .summary(portfolio.getSummary())
                .description(portfolio.getDescription())
                .mainCategory(portfolio.getMainCategory())
                .subCategory(portfolio.getSubCategory())
                .contribution(portfolio.getContribution())
                .imageUrl(portfolio.getImage())
                .imageName(imageName)
                .generatedByAi(portfolio.isGeneratedByAi())
                .createdAt(portfolio.getCreatedAt())
                .build();
    }
}
