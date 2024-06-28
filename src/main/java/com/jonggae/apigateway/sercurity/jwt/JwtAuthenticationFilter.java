package com.jonggae.apigateway.sercurity.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonggae.apigateway.customer.dto.LoginRequestDto;
import com.jonggae.apigateway.sercurity.handler.login.LoginFailureHandler;
import com.jonggae.apigateway.sercurity.handler.login.LoginSuccessHandler;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    public JwtAuthenticationFilter(ReactiveAuthenticationManager authenticationManager, LoginSuccessHandler successHandler, LoginFailureHandler failureHandler) {
        super(authenticationManager);
        this.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/api/customer/login"));
        this.setServerAuthenticationConverter(new ServerAuthenticationConverter() {
            @Override
            public Mono<Authentication> convert(ServerWebExchange exchange) {
                return exchange.getRequest().getBody().next().flatMap(dataBuffer -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        LoginRequestDto loginRequestDto = objectMapper.readValue(dataBuffer.asInputStream(), LoginRequestDto.class);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                loginRequestDto.getCustomerName(),
                                loginRequestDto.getPassword()
                        );
                        return Mono.just(authentication);
                    } catch (IOException e) {
                        return Mono.error(new AuthenticationServiceException("로그인 실패", e));
                    }
                });
            }
        });
        this.setAuthenticationSuccessHandler(successHandler);
        this.setAuthenticationFailureHandler(failureHandler);
    }
}
