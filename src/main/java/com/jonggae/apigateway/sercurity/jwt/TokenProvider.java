package com.jonggae.apigateway.sercurity.jwt;

import com.jonggae.apigateway.sercurity.utils.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
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
            ReactiveRedisTemplate<String, String> redisTemplate,
            @Value("${jwt.secret.key}") String secretKey,
            @Value("${jwt.refresh.secret.key}") String refreshSecretKey,
            @Value("${jwt.expiration_time}") long tokenExpirationTime,
            @Value("${jwt.refresh_expiration_time}") long refreshTokenExpirationTime,
            CustomUserDetailsService customUserDetailsService) {
        this.redisTemplate = redisTemplate;
        this.secretKey = secretKey;
        this.refreshSecretKey = refreshSecretKey;
        this.tokenExpirationTime = tokenExpirationTime * 1000; // 10분으로 설정함
        this.refreshTokenExpirationTime = refreshTokenExpirationTime * 1000; // 1시간으로 redis 저장
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        logger.debug("Signing key initialized with hash: {}", Arrays.hashCode(keyBytes));


        byte[] refreshKeyBytes = Decoders.BASE64.decode(refreshSecretKey);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
        logger.debug("Refresh signing key initialized with hash: {}", Arrays.hashCode(refreshKeyBytes));

    }

    // 액세스 토큰을 생성함
    public String createAccessToken(Authentication authentication, Long customerId) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenExpirationTime);

        String token =  Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(CUSTOMER_ID_KEY, customerId)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
        logger.debug("Access token created with claims: auth={}, customerId={}, expiration={}", authorities, customerId, validity);
        logger.debug("Access token: {}", token.substring(0, Math.min(50, token.length())) + "...");
        return token;
    }

    // 리프래시 토큰을 생성함
    public String createRefreshToken(Authentication authentication, Long customerId) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenExpirationTime);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(CUSTOMER_ID_KEY, customerId)
                .signWith(refreshKey, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Mono<Authentication> getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        logger.debug("Claims parsed from token: {}", claims);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return customUserDetailsService.findByUsername(claims.getSubject())
                .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, token, authorities));
    }

    public Mono<Authentication> getAuthenticationFromRefreshToken(String refreshToken) {
        return Mono.fromCallable(() -> Jwts.parserBuilder()
                        .setSigningKey(refreshKey)
                        .build()
                        .parseClaimsJws(refreshToken)
                        .getBody())
                .flatMap(claims -> customUserDetailsService.findByUsername(claims.getSubject())
                        .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, refreshToken, userDetails.getAuthorities()))
                );
    }

    public Mono<Long> getCustomerIdFromToken(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get(CUSTOMER_ID_KEY, Long.class);
        });
    }

    public Mono<String> getCustomerNameFromToken(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        });
    }

    public Mono<Boolean> validateToken(String token) {
        logger.debug("Validating token: {}", token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return Mono.just(true);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 AccessToken 서명입니다: {}", token);
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다: {}", token);
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다: {}", token);
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다: {}", token);
        }
        return Mono.just(false);
    }


public Mono<Boolean> validateRefreshToken(String token) {
    return redisTemplate.hasKey(token)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.just(false);
                }

                return Mono.justOrEmpty(token)
                        .map(t -> {
                            try {
                                Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(t);
                                logger.info("유효한 리프레시 토큰입니다.");

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
                            return false;
                        });
            });
}
}
