package com.paassible.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "recruit-stack-service", url = "${recruit-service.url}")
public interface StackClient {

    @PostMapping("/recruits/internal/stack")
    List<String> getStackNames(@RequestBody List<Long> stackIds);
}