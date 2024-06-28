package com.jonggae.apigateway.customer.service;

import com.jonggae.apigateway.customer.dto.CustomerRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final WebClient.Builder webClientBuilder;

    // 회원가입 요청
    public Mono<String> save(CustomerRequestDto customerRequestDto) {
        String userServiceUrl = "http://localhost:8081/api/customer/register";
        return webClientBuilder.build()
                .post()
                .uri(userServiceUrl)
                .bodyValue(customerRequestDto)
                .retrieve()
                .bodyToMono(String.class);
    }
}
