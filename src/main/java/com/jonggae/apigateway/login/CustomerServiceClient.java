package com.jonggae.apigateway.login;

import com.jonggae.apigateway.security.CustomUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceClient {

    private final WebClient.Builder webClientBuilder;

    public CustomerServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<CustomUserDetails> loadUserByUsername(String username) {
        return webClientBuilder.build()
                .get()
                .uri("http://customer-service/api/customers/{username}",username)
                .retrieve()
                .bodyToMono(CustomUserDetails.class);
    }
}
