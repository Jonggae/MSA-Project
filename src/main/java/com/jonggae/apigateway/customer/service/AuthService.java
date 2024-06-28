package com.jonggae.apigateway.customer.service;

import com.jonggae.apigateway.customer.dto.CustomerResponseDto;
import com.jonggae.apigateway.customer.dto.JwtResponse;
import com.jonggae.apigateway.customer.dto.LoginRequestDto;
import com.jonggae.apigateway.sercurity.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final WebClient.Builder webClientBuilder;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;

    public Mono<ResponseEntity<JwtResponse>> authenticate(LoginRequestDto loginRequestDto) {
        String customerServiceUrl = "http://customer-service/customers/login";
        return webClientBuilder.build()
                .post()
                .uri(customerServiceUrl)
                .bodyValue(loginRequestDto)
                .retrieve()
                .bodyToMono(CustomerResponseDto.class)
                .flatMap(customer -> {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getCustomerName(), loginRequestDto.getPassword()
                    );
                    return reactiveAuthenticationManager.authenticate(authentication)
                            .flatMap(auth -> {
                                String accessToken = tokenProvider.createAccessToken(auth);
                                String refreshToken = tokenProvider.createRefreshToken(auth);
                                return Mono.just(ResponseEntity.ok(new JwtResponse(accessToken, refreshToken)));
                            });
                })
                .onErrorResume(e -> {
                    JwtResponse errorResponse = new JwtResponse("Login failed", null);
                    return Mono.just(ResponseEntity.status(401).body(errorResponse));
                });
    }
}
