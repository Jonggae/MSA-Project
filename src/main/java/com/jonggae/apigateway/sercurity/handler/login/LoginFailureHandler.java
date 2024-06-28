package com.jonggae.apigateway.sercurity.handler.login;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class LoginFailureHandler implements ServerAuthenticationFailureHandler {

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange exchange, AuthenticationException exception) {
        exchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getExchange().getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String jsonPayload = "{\"message\": \"" + exception.getMessage() + "\", \"error\": \"Id, 비밀번호를 확인해 주세요\"}";

        return exchange.getExchange().getResponse().writeWith(
                Mono.just(exchange.getExchange().getResponse().bufferFactory().wrap(jsonPayload.getBytes(StandardCharsets.UTF_8)))
        );
    }

}
