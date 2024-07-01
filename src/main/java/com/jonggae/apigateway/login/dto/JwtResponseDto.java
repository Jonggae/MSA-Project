package com.jonggae.apigateway.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JwtResponseDto {
    private String accessToken;
    private String refreshToken;
}
