package com.paassible.userservice.user.dto;

import lombok.Getter;

@Getter
public class PortfolioRequest {
    private Long positionId;
    private String title;
    private String summary;
    private String description;
}
