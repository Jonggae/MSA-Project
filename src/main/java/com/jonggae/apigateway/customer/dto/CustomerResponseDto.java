package com.jonggae.apigateway.customer.dto;

import com.jonggae.apigateway.customer.entity.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class CustomerResponseDto {
    private String customerName;
    private String password;
    private String email;
    private Set<Authority> authorities;
    private boolean enabled;
}
