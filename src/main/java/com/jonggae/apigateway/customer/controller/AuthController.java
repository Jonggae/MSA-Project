package com.jonggae.apigateway.customer.controller;

import com.jonggae.apigateway.customer.dto.CustomerRequestDto;
import com.jonggae.apigateway.customer.dto.LoginRequestDto;
import com.jonggae.apigateway.customer.service.AuthService;
import com.jonggae.apigateway.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final CustomerService customerService;
    private AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@RequestBody CustomerRequestDto customerRequestDto) {
        customerService.save(customerRequestDto);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        String token = authService.authenticate(loginRequestDto);
        return ResponseEntity.ok(token);
    }
}
