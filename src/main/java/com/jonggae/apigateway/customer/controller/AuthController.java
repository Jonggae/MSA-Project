package com.jonggae.apigateway.customer.controller;

import com.jonggae.apigateway.customer.dto.JwtResponse;
import com.jonggae.apigateway.customer.dto.LoginRequestDto;
import com.jonggae.apigateway.customer.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/auth/login")
    public Mono<ResponseEntity<JwtResponse>> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        return authService.authenticate(loginRequestDto);
    }
}
