package com.paassible.meetservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.paassible.meetservice.chat.dto.ChatMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean("chatRedis")
    public RedisTemplate<String, ChatMessage> chatRedisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, ChatMessage> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);

        t.setKeySerializer(new StringRedisSerializer());

        ObjectMapper om = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .build();

        Jackson2JsonRedisSerializer<ChatMessage> valueSer =
                new Jackson2JsonRedisSerializer<>(om, ChatMessage.class);

        t.setValueSerializer(valueSer);
        t.setHashKeySerializer(new StringRedisSerializer());
        t.setHashValueSerializer(valueSer);
        t.afterPropertiesSet();
        return t;
    }

    @Primary
    @Bean("stringRedis")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
        return new StringRedisTemplate(cf);
    }

}
