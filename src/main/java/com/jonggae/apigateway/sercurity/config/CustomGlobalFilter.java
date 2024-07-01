package com.jonggae.apigateway.sercurity.config;

import com.jonggae.apigateway.sercurity.jwt.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    private final Logger logger = LoggerFactory.getLogger(CustomGlobalFilter.class);

    private final TokenProvider tokenProvider;

    @Autowired
    public CustomGlobalFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractToken(exchange.getRequest().getHeaders());
        if (token != null) {
            logger.debug("Extracted token: {}", token);

            return tokenProvider.validateToken(token)
                    .flatMap(isValid -> {
                        if (isValid) {
                            logger.debug("Token is valid");
                            return tokenProvider.getCustomerIdFromToken(token)
                                    .flatMap(customerId -> tokenProvider.getCustomerNameFromToken(token)
                                            .flatMap(customerName -> {
                                                logger.debug("CustomerId: {}, CustomerName: {}", customerId, customerName);

                                                exchange.getRequest().mutate()
                                                        .header("X-Customer-Id", String.valueOf(customerId))
                                                        .header("X-Customer-Name", customerName)
                                                        .build();
                                                return chain.filter(exchange);
                                            }));
                        } else {
                            logger.error("Invalid token");
                            return chain.filter(exchange);
                        }
                    });
        }
        return chain.filter(exchange);
    }

    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -1; // 필터 순서 설정
    }
}
