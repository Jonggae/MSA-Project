//package com.jonggae.apigateway.sercurity.jwt;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextImpl;
//import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
//import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthorizationFilter implements WebFilter {
//
//    private final TokenProvider tokenProvider;
//    private final JwtFilter jwtFilter;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        return ServerWebExchangeMatchers.pathMatchers("/api/**")
//                .matches(exchange)
//                .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
//                .flatMap(matchResult -> extractToken(exchange))
//                .flatMap(token -> tokenProvider.validateToken(token)
//                        .filter(valid -> valid)
//                        .flatMap(valid -> tokenProvider.getAuthentication(token))
//                )
//                .flatMap(authentication -> {
//                    SecurityContext context = new SecurityContextImpl(authentication);
//                    return chain.filter(exchange)
//                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
//                })
//                .switchIfEmpty(chain.filter(exchange));
//    }
//
//    private Mono<String> extractToken(ServerWebExchange exchange) {
//        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("Authorization"))
//                .filter(bearerToken -> bearerToken.startsWith("Bearer "))
//                .map(bearerToken -> bearerToken.substring(7));
//    }
//}
