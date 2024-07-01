package com.jonggae.apigateway.login.service;

import com.jonggae.apigateway.common.redis.TokenService;
import com.jonggae.apigateway.login.dto.CustomerResponseDto;
import com.jonggae.apigateway.login.dto.JwtResponseDto;
import com.jonggae.apigateway.login.dto.LoginRequestDto;
import com.jonggae.apigateway.sercurity.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
public class LoginService {
    private final WebClient.Builder webClientBuilder;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public Mono<ResponseEntity<JwtResponseDto>> authenticate(LoginRequestDto loginRequestDto) {
        String customerServiceUrl = "http://customer-service/api/customers/" + loginRequestDto.getCustomerName();
        return webClientBuilder.build()
                .get()
                .uri(customerServiceUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
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
                            customer.getCustomerName(), null, authorities
                    );

                    // JWT 토큰 생성
                    String accessToken = tokenProvider.createAccessToken(authentication, customer.getCustomerId());
                    String refreshToken = tokenProvider.createRefreshToken(authentication, customer.getCustomerId());

                    // Refresh 토큰 저장
                    tokenService.saveRefreshToken(refreshToken, customer.getCustomerName());

                    // JWT 응답 생성
                    JwtResponseDto jwtResponseDto = new JwtResponseDto(accessToken, refreshToken);

                    return Mono.just(ResponseEntity.ok(jwtResponseDto));
                })
                .onErrorResume(e -> {
                    JwtResponseDto errorResponse = new JwtResponseDto("Login failed", null);
                    return Mono.just(ResponseEntity.status(401).body(errorResponse));
                });
    }
}
