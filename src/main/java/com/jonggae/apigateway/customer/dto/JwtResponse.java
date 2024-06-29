package com.jonggae.apigateway.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
}
