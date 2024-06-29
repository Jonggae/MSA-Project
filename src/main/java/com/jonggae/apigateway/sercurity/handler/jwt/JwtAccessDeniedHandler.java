package com.jonggae.apigateway.sercurity.handler.jwt;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonggae.apigateway.common.apiResponse.ApiResponseDto;
import com.jonggae.apigateway.common.apiResponse.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAccessDeniedHandler implements ServerAccessDeniedHandler {
    // JWT 인증 값에 [권한]이 없는 접근을 할 때 403
    // eg) admin 권한을 customer 가 접근 하였을 때 Security 레벨

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        ResponseEntity<ApiResponseDto<Object>> errorResponse = ApiResponseUtil.error(
                "접근 권한이 없습니다", 403, "ACCESS_DENIED", "해당 작업은 관리자만 가능합니다");

        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().setAcceptCharset(List.of(StandardCharsets.UTF_8));

        try {
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(
                            new ObjectMapper().writeValueAsBytes(errorResponse)
                    ))
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
