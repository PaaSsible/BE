package com.paassible.recruitservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.paassible.recruitservice", "com.paassible.common"})
public class RecruitServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitServiceApplication.class, args);
    }

}
