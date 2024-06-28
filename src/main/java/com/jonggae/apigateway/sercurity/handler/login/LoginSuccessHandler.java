package com.jonggae.apigateway.sercurity.handler.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonggae.apigateway.common.redis.TokenService;
import com.jonggae.apigateway.sercurity.jwt.TokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LoginSuccessHandler implements ServerAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    public LoginSuccessHandler(TokenProvider tokenProvider, TokenService tokenService) {
        this.tokenProvider = tokenProvider;
        this.tokenService = tokenService;
    }


    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        String customerName = authentication.getName();
        tokenService.saveRefreshToken(refreshToken, customerName);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        String jsonPayload = null;
        try {
            jsonPayload = new ObjectMapper().writeValueAsString(tokens);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String finalJsonPayload = jsonPayload;
        return webFilterExchange.getExchange().getResponse().writeWith(
                Mono.fromSupplier(() -> {
                    webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
                    webFilterExchange.getExchange().getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    webFilterExchange.getExchange().getResponse().getHeaders().setAcceptCharset(List.of(StandardCharsets.UTF_8));
                    return webFilterExchange.getExchange().getResponse().bufferFactory().wrap(finalJsonPayload.getBytes(StandardCharsets.UTF_8));
                })
        );
    }
}
