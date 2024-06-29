package com.jonggae.apigateway.common.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/*
 * Redis로 이메일 인증 토큰, 리프레시 토큰을 관리함
 */
@Service
@AllArgsConstructor
public class TokenService {
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofHours(1);
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<Boolean> saveRefreshToken(String refreshToken, String customerName) {
        return reactiveRedisTemplate.opsForValue().set(refreshToken, customerName, REFRESH_TOKEN_TTL);
    }

    public Mono<String> getCustomerNameByRefreshToken(String refreshToken) {
        return reactiveRedisTemplate.opsForValue().get(refreshToken);
    }

    public Mono<Boolean> deleteRefreshToken(String refreshToken) {
        return reactiveRedisTemplate.delete(refreshToken).map(count -> count > 0);
    }
}
