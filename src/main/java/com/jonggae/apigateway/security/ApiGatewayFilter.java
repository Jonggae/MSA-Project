package com.jonggae.apigateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ApiGatewayFilter extends AbstractGatewayFilterFactory<ApiGatewayFilter.Config> {

    private final JwtTokenUtil jwtTokenUtil;
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayFilter.class);

    public static class Config {
    }

    public ApiGatewayFilter(JwtTokenUtil jwtTokenUtil) {
        super(Config.class);
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            logger.info("Request path: {}", path);

            // 로그인 경로는 JWT 검증을 하지 않음
            if (path.equals("/api/customers/login") || path.equals("/api/customers/register")
                    || path.startsWith("/api/products/") || path.startsWith("/api/test")) {
                return chain.filter(exchange);
            }
            String token = extractToken(exchange.getRequest().getHeaders());
            if (token == null) {
                logger.warn("토큰이 존재하지 않습니다.");
                return chain.filter(exchange);
            }
            try {
                if (!jwtTokenUtil.validateToken(token)) {
                    logger.warn("Invalid JWT token");
                    return chain.filter(exchange);
                }
                Authentication authentication = jwtTokenUtil.getAuthentication(token);
                String customerId = jwtTokenUtil.getCustomerIdFromToken(token);
                String customerName = jwtTokenUtil.getCustomerNameFromToken(token);
                logger.info("Extracted customerId: {} and customerName: {}", customerId, customerName);

                exchange = exchange.mutate()
                        .request(r -> r
                                .header("customerId", customerId)
                                .header("customerName", customerName)
                        )
                        .build();

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            } catch (Exception e) {
                logger.error("Error processing JWT token", e);
                return chain.filter(exchange); // Spring Security에 처리 위임
            }
        };

    }

    private String extractToken(HttpHeaders headers) {
        String bearerToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        DataBuffer buffer = response.bufferFactory().wrap(err.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

}
