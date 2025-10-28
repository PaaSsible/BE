package com.paassible.boardservice.portfolio.config;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GeminiConfig {

    @Value("${google.gemini.api-key}")
    private String apiKey;

    private final PromptProvider promptProvider;

    @Bean
    public Client geminiClient() {
        return Client.builder()
                .apiKey(apiKey)
                .build();
    }

    @Bean
    public GenerateContentConfig generationConfig() {
        return GenerateContentConfig.builder()
                .systemInstruction(
                        Content.fromParts(
                                Part.fromText(promptProvider.portfolioSystemPrompt())
                        )
                )
                .build();
    }
}
