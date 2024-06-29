package com.jonggae.apigateway.sercurity.controller;


import com.jonggae.apigateway.common.redis.TokenService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
// todo: response 응답 형태 정리하기

@RestController
@AllArgsConstructor
public class LogoutController {
    private static final Logger logger = LoggerFactory.getLogger(LogoutController.class);
    private final TokenService tokenService;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @PostMapping("/auth/logout")
    public Mono<ResponseEntity<String>> logout(ServerWebExchange exchange, Authentication authentication) {
        String customerName = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            customerName = ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        String refreshToken = getCookieValue(exchange);
        String accessToken = getHeaderToken(exchange);

        Mono<Boolean> refreshTokenDeletion = Mono.just(true);
        if (refreshToken != null) {
            refreshTokenDeletion = tokenService.deleteRefreshToken(refreshToken)
                    .thenReturn(true);
        }

        Mono<Boolean> accessTokenBlacklisting = Mono.just(true);
        if (accessToken != null) {
            long accessTokenExpirationMillis = 600000;
            accessTokenBlacklisting = reactiveRedisTemplate.opsForValue()
                    .set(accessToken, "blacklisted")
                    .flatMap(success -> reactiveRedisTemplate.expire(accessToken, Duration.ofMillis(accessTokenExpirationMillis)).thenReturn(true));
        }

        return Mono.zip(refreshTokenDeletion, accessTokenBlacklisting)
                .then(Mono.just(ResponseEntity.ok((customerName != null ? customerName : "사용자") + " 로그아웃 되었습니다.")));
    }

    private String getCookieValue(ServerWebExchange exchange) {
        return Objects.requireNonNull(exchange.getRequest().getCookies().getFirst("refreshToken")).getValue();
    }

    private String getHeaderToken(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

