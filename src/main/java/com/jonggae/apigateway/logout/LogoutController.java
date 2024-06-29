package com.jonggae.apigateway.logout;


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
    private final LogoutService logoutService;

    @PostMapping("/auth/logout")
    public Mono<ResponseEntity<String>> logout(ServerWebExchange exchange, Authentication authentication) {
        return logoutService.logout(exchange, authentication)
                .map(customerName -> ResponseEntity.ok((customerName != null ? customerName : "사용자") + " 로그아웃 되었습니다."));
    }

}

