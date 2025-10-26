package com.paassible.meetservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@ConfigurationPropertiesScan
@EnableRetry
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"com.paassible.meetservice", "com.paassible.common"})
@EnableFeignClients(basePackages = "com.paassible.meetservice.client")
public class MeetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetServiceApplication.class, args);
    }

}

