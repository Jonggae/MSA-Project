package com.jonggae.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {

    private final SecretKey jwtSecretKey;
    private final SecretKey jwtRefreshSecretKey;
    private final int jwtExpirationInMs;
    private final int jwtRefreshExpirationInMs;

    public TokenProvider(
            @Value("${jwt.secret.key}") String jwtSecret,
            @Value("${jwt.refresh.secret.key}") String jwtRefreshSecret,
            @Value("${jwt.expiration_time}") int jwtExpirationInMs,
            @Value("${jwt.refresh_expiration_time}") int jwtRefreshExpirationInMs) {
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtRefreshSecretKey = Keys.hmacShaKeyFor(jwtRefreshSecret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs * 1000; // Convert to milliseconds
        this.jwtRefreshExpirationInMs = jwtRefreshExpirationInMs * 1000; // Convert to milliseconds
    }

    public String generateAccessToken(CustomUserDetails userDetails) {
        return generateToken(userDetails, jwtSecretKey, jwtExpirationInMs);
    }

    public String generateRefreshToken(CustomUserDetails userDetails) {
        return generateToken(userDetails, jwtRefreshSecretKey, jwtRefreshExpirationInMs);
    }

    private String generateToken(CustomUserDetails userDetails, SecretKey secretKey, int expirationInMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs); // 환경 변수 단위가 초라면 변환 필요

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("customerId", userDetails.getCustomerId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token, boolean isRefreshToken) {
        try {
            SecretKey secretKey = isRefreshToken ? jwtRefreshSecretKey : jwtSecretKey;
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserIdFromJWT(String token, boolean isRefreshToken) {
        SecretKey secretKey = isRefreshToken ? jwtRefreshSecretKey : jwtSecretKey;
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    public String getCustomerIdFromJWT(String token, boolean isRefreshToken) {
        SecretKey secretKey = isRefreshToken ? jwtRefreshSecretKey : jwtSecretKey;
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("customerId", String.class);
    }

}
