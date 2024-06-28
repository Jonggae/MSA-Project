package com.jonggae.apigateway.sercurity.handler.jwt;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonggae.apigateway.common.apiResponse.ApiResponseDto;
import com.jonggae.apigateway.common.apiResponse.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    /*
    인증되지 않은 접근 401 (JWT 토큰이 유효하지 않을 때)
    * 서명, 잘못된 토큰 등등 */

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ResponseEntity<ApiResponseDto<Object>> errorResponse = ApiResponseUtil.error(
                "잘못된 인증 정보입니다",
                401,
                "UNAUTHORIZED",
                null);

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().setAcceptCharset(List.of(StandardCharsets.UTF_8));

        try {
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(
                            new ObjectMapper().writeValueAsBytes(errorResponse)
                    ))
            );
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
