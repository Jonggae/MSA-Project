package com.jonggae.apigateway.logout.service;


import com.jonggae.apigateway.common.redis.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final TokenService tokenService;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<String> logout(ServerWebExchange exchange, Authentication authentication) {
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
        String finalCustomerName = customerName;
        return Mono.zip(refreshTokenDeletion, accessTokenBlacklisting)
                .then(Mono.defer(() -> {
                    assert finalCustomerName != null;
                    return Mono.just(finalCustomerName);
                }));
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
