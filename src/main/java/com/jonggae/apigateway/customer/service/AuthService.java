package com.jonggae.apigateway.customer.service;

import com.jonggae.apigateway.customer.dto.CustomerResponseDto;
import com.jonggae.apigateway.customer.dto.JwtResponse;
import com.jonggae.apigateway.customer.dto.LoginRequestDto;
import com.jonggae.apigateway.sercurity.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final WebClient.Builder webClientBuilder;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public Mono<ResponseEntity<JwtResponse>> authenticate(LoginRequestDto loginRequestDto) {
        String customerServiceUrl = "http://customer-service/api/customers/login";
        return webClientBuilder.build()
                .post()
                .uri(customerServiceUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 필요한 헤더 추가
                .bodyValue(loginRequestDto)
                .retrieve()
                .bodyToMono(CustomerResponseDto.class)
                .flatMap(customer -> {

                    if (!passwordEncoder.matches(loginRequestDto.getPassword(), customer.getPassword())) {
                        return Mono.error(new BadCredentialsException("Invalid password"));
                    }
                    List<GrantedAuthority> authorities = customer.getAuthorityDtoSet().stream()
                            .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName().name()))
                            .collect(Collectors.toList());

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            customer.getCustomerName(), loginRequestDto.getPassword(), authorities
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
