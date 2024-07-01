package com.jonggae.apigateway.sercurity.controller;

import com.jonggae.apigateway.common.redis.TokenService;
import com.jonggae.apigateway.sercurity.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

// todo: response 응답 형태 정리하기
@RestController
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenController {

    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    // 재발급 받는 과정
    @PostMapping("/auth/refresh")
    public Mono<ResponseEntity<?>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid refresh token"));
        }

        return tokenProvider.validateRefreshToken(refreshToken)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.just(ResponseEntity.badRequest().body("Invalid refresh token"));
                    }

                    return tokenProvider.getCustomerNameFromToken(refreshToken)
                            .flatMap(customerName -> {
                                return tokenService.getCustomerNameByRefreshToken(refreshToken)
                                        .flatMap(storedCustomerName -> {
                                            if (storedCustomerName == null || !storedCustomerName.equals(customerName)) {
                                                log.info("저장된 고객 이름이 토큰의 고객 이름과 일치하지 않습니다: {}", customerName);
                                                return Mono.just(ResponseEntity.badRequest().body("Invalid refresh token"));
                                            }

                                            return tokenProvider.getCustomerIdFromToken(refreshToken)
                                                    .flatMap(customerId -> tokenProvider.getAuthenticationFromRefreshToken(refreshToken)
                                                            .map(authentication -> {
                                                                String newAccessToken = tokenProvider.createAccessToken(authentication, customerId);
                                                                return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
                                                            }));
                                        });
                            });
                });
    }
}
