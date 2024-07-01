package com.jonggae.apigateway.login.contorller;

import com.jonggae.apigateway.login.service.LoginService;
import com.jonggae.apigateway.login.dto.JwtResponseDto;
import com.jonggae.apigateway.login.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;


    @PostMapping("/auth/login")
    public Mono<ResponseEntity<JwtResponseDto>> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        return loginService.authenticate(loginRequestDto);
    }
}
