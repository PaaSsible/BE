package com.paassible.boardservice.portfolio.config;

import org.springframework.stereotype.Component;

@Component
public class PromptProvider {

    public String portfolioSystemPrompt() {
        return """ 
        너는 개발자 포트폴리오를 작성하는 전문가다.
        다음 데이터를 기반으로 **정돈된 한국어 포트폴리오 섹션**을 완성하라.
        """;
    }
}

