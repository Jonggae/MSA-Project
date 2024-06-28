package com.jonggae.apigateway.customer.service;

import com.jonggae.apigateway.customer.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;

    public String authenticate(LoginRequestDto loginRequestDto){
        String customerServiceUrl = "http://localhost:8081/api/customer/login";
        ResponseEntity<String> response = restTemplate.postForEntity(customerServiceUrl, loginRequestDto, String.class);
        return response.getBody();
    }
}
