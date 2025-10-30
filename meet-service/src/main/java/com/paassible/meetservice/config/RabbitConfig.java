package com.paassible.meetservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "notifications";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(EXCHANGE);
    }
}


