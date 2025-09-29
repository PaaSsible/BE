package com.paassible.common.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paassible.common.response.ErrorCode;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class FeignClientErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {

            String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));

            JsonNode root = objectMapper.readTree(body);
            String errorCodeStr = root.path("code").asText();

            ErrorCode errorCode = ErrorCode.fromCode(errorCodeStr);

            return new CustomException(errorCode);
        } catch (Exception e) {
            return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

