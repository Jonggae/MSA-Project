package com.jonggae.apigateway.customer.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
}
