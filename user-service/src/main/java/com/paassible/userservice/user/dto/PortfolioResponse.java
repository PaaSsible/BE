package com.paassible.userservice.user.dto;

import com.paassible.userservice.user.entity.Portfolio;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortfolioResponse {
    private Long id;
    private Long userId;
    private String positionName;
    private String title;
    private String summary;
    private String description;

    public static PortfolioResponse from(Portfolio portfolio, String positionName) {
        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .userId(portfolio.getUserId())
                .positionName(positionName)
                .title(portfolio.getTitle())
                .summary(portfolio.getSummary())
                .description(portfolio.getDescription())
                .build();
    }
}