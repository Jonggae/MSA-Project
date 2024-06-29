package com.jonggae.apigateway.login;

import com.jonggae.apigateway.customer.dto.JwtResponse;
import com.jonggae.apigateway.customer.dto.LoginRequestDto;
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
    public Mono<ResponseEntity<JwtResponse>> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        return loginService.authenticate(loginRequestDto);
    }
}
