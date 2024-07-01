package com.jonggae.apigateway.sercurity.jwt;

import com.jonggae.apigateway.sercurity.utils.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final TokenProvider tokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("Authorization"))
                .filter(bearerToken -> {
                    boolean startsWithBearer = bearerToken.startsWith("Bearer ");
                    if (!startsWithBearer) {
                        logger.info("Authorization header does not start with Bearer");
                    }
                    return startsWithBearer;
                })
                .map(bearerToken -> bearerToken.substring(7))
                .flatMap(token -> {
                    logger.debug("Validating token: {}", token);
                    return tokenProvider.validateToken(token)
                            .flatMap(valid -> {
                                if (valid) {
                                    logger.debug("Token is valid");
                                    return tokenProvider.getAuthentication(token)
                                            .flatMap(authentication -> {
                                                logger.debug("Authentication successful: {}", authentication);
                                                SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
                                                return tokenProvider.getCustomerIdFromToken(token)
                                                        .flatMap(customerId -> tokenProvider.getCustomerNameFromToken(token)
                                                                .flatMap(customerName -> {
                                                                    exchange.getRequest().mutate()
                                                                            .header("X-Customer-Id", String.valueOf(customerId))
                                                                            .header("X-Customer-Name", customerName)
                                                                            .build();
                                                                    return chain.filter(exchange)
                                                                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                                                                }));
                                            });
                                } else {
                                    logger.info("Invalid JWT token: {}", token);
                                    return Mono.empty();
                                }
                            });
                })
                .onErrorResume(e -> {
                    logger.error("JWT validation error", e);
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}