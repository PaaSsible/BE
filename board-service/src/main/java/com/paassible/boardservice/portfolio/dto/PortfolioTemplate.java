package com.paassible.boardservice.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PortfolioTemplate {

    public String buildPrompt(
            String projectName,
            String position,
            List<FeatureData> features,
            String taskCompletion,
            String attendanceRate,
            String value,
            String total,
            String contribution
    ) {
        StringBuilder featureSection = new StringBuilder();

        for (FeatureData feature : features) {
            featureSection.append(String.format("""
                    ### 기능: %s
                    
                    설명: %s
                    
                    설명이 기술적 세부 내용이 부족하거나 단순히 구현 사실만 언급하는 경우,
                    아래 항목(문제·해결·성과)을 작성하지 말고,
                    해당 기능의 역할과 구현 의도를 간결히 요약하라.
            
                    위 설명을 분석하여 아래 항목을 작성하라.
                    - **문제:** 설명 내용에서 해당 기능 구현 중 발생했을 법한 핵심 이슈를 추론하라.
                    - **해결:** 설명 속 기술적 접근 또는 해결 방식을 요약하라.
                    - **성과:** 설명의 결과로 예상되는 개선 효과나 프로젝트 내 기여도를 작성하라.
            
                    ---
                    """,
                    feature.getTitle(),
                    feature.getDescription() == null ? "(설명 없음)" : feature.getDescription()
            ));
        }

        return String.format("""
            아래 프로젝트 정보를 기반으로 Markdown 형식의 구조화된 포트폴리오를 완성하라.

            ---
            📌 프로젝트명: %s
            👤 역할: %s

            ---
            🧭 프로젝트 개요
            아래의 기능 목록 전체를 분석하여,
            이 프로젝트가 어떤 문제를 해결하기 위해 만들어졌는지,
            어떤 사용자를 대상으로 하는지,
            그리고 어떤 가치를 제공하는지 요약하라.
            
            ---
            ✅ 담당 기능 (기능별 문제/해결/성과를 각각 작성)

            %s

            ---
            📈 성과
            작업률 : %s
            회의 참석률 : %s
            커뮤니케이션 빈도 : %s/%s
            기여도 : %s

            💬 기술 스택
            기능 설명 기반으로 자동 분석

            ---
            작성 지침:
            1. 각 기능은 제목–문제–해결–성과 순서로 정리하라.
            2. 각 항목은 한 문단씩 작성하라.
            3. 기술 스택은 기능 전반에 사용된 공통 기술을 추론해 정리하라.
            4. 불필요한 수식어나 장식은 제외하고, 명확하고 간결하게 작성하라.
            """,
                projectName,
                position,
                featureSection,
                taskCompletion,
                attendanceRate,
                value,
                total,
                contribution
        );
    }

    @Getter
    @AllArgsConstructor
    public static class FeatureData {
        private final String title;
        private final String description;
    }
}
