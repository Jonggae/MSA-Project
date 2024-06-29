package com.jonggae.apigateway.sercurity.utils;

import com.jonggae.apigateway.customer.dto.CustomerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements ReactiveUserDetailsService, UserDetailsService {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<UserDetails> findByUsername(String customerName) {
        String customerServiceUrl = "http://customer-service/api/customers/" + customerName;
        return webClientBuilder.build()
                .get()
                .uri(customerServiceUrl)
                .retrieve()
                .bodyToMono(CustomerResponseDto.class)
                .doOnNext(customer -> {
                    System.out.println("Customer retrieved: " + customer);
                })
                .map(customer -> {
                    List<GrantedAuthority> authorities = customer.getAuthorityDtoSet().stream()
                            .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName().name()))
                            .collect(Collectors.toList());
                    return User.withUsername(customer.getCustomerName())
                            .password(customer.getPassword())
                            .authorities(authorities)
                            .accountExpired(false)
                            .accountLocked(false)
                            .credentialsExpired(false)
                            .disabled(!customer.isEnabled())
                            .build();
                })
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username).block();
    }
}
