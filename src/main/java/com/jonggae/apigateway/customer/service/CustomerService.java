package com.jonggae.apigateway.customer.service;

import com.jonggae.apigateway.customer.dto.CustomerRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final RestTemplate restTemplate;

    public void save(CustomerRequestDto customerRequestDto) {
        String userServiceUrl = "http://localhost:8081/api/customer/register";
        ResponseEntity<String> response = restTemplate.postForEntity(userServiceUrl, customerRequestDto, String.class);
        response.getBody();
    }
}
