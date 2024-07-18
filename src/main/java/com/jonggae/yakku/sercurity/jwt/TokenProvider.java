package com.jonggae.yakku.sercurity.jwt;

import com.jonggae.yakku.sercurity.utils.CustomUserDetails;
import com.jonggae.yakku.sercurity.utils.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {

    private final RedisTemplate<String, String> redisTemplate;
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private static final String CUSTOMER_ID_KEY = "customerId";
    private final String secretKey;
    private final String refreshSecretKey;
    private final long tokenExpirationTime;
    private final long refreshTokenExpirationTime;
    private final CustomUserDetailsService customUserDetailsService;
    private Key key;
    private Key refreshKey;

    public TokenProvider(
            RedisTemplate<String, String> redisTemplate,
            @Value("${jwt.secret.key}") String secretKey,
            @Value("${jwt.refresh.secret.key}") String refreshSecretKey,
            @Value("${jwt.expiration_time}") long tokenExpirationTime,
            @Value("${jwt.refresh_expiration_time}") long refreshTokenExpirationTime, CustomUserDetailsService customUserDetailsService) {
        this.redisTemplate = redisTemplate;
        this.secretKey = secretKey;
        this.refreshSecretKey = refreshSecretKey;
        this.tokenExpirationTime = tokenExpirationTime * 1000; // 60 = 1분
        this.refreshTokenExpirationTime = refreshTokenExpirationTime * 1000; // 예: 604800초 = 7일
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);

        byte[] refreshKeyBytes = Decoders.BASE64.decode(refreshSecretKey);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    // 액세스 토큰을 생성함
    public String createAccessToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long customerId = userDetails.getCustomerId();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenExpirationTime);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(CUSTOMER_ID_KEY, customerId)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    // 리프래시 토큰을 생성함
    public String createRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long customerId = userDetails.getCustomerId();

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenExpirationTime);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(CUSTOMER_ID_KEY, customerId)
                .signWith(refreshKey, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public Authentication getAuthenticationFromRefreshToken(String refreshToken) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, refreshToken, userDetails.getAuthorities());
    }

    public String getCustomerNameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 AccessToken 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public boolean validateRefreshToken(String token) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(token))) {
            try {
                Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
                return true;
            } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
                logger.info("잘못된 RefreshToken 서명입니다.");
            } catch (ExpiredJwtException e) {
                logger.info("만료된 JWT 리프레시 토큰입니다.");
            } catch (UnsupportedJwtException e) {
                logger.info("지원되지 않는 JWT 리프레시 토큰입니다.");
            } catch (IllegalArgumentException e) {
                logger.info("JWT 리프레시 토큰이 잘못되었습니다.");
            }

        }else{
            logger.info("Redis에 토큰 정보가 없습니다.");
        }
        return false;

    }
}
