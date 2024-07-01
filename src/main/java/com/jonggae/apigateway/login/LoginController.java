package com.jonggae.apigateway.login;

import com.jonggae.apigateway.common.redis.TokenService;
import com.jonggae.apigateway.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final CustomerServiceClient customerServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public Mono<ResponseEntity<JwtAuthenticationResponse>> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return customerServiceClient.loadUserByUsername(loginRequest.getUsername())
                .flatMap(userDetails -> {
                    if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                        return Mono.error(new RuntimeException("Invalid username or password"));
                    }
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            loginRequest.getPassword(),
                            userDetails.getAuthorities()
                    );
                    Authentication authResult = authenticationManager.authenticate(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authResult);

                    String accessToken = tokenProvider.generateAccessToken(userDetails);
                    String refreshToken = tokenProvider.generateRefreshToken(userDetails);

                    // 리프레시 토큰을 Redis에 저장
                    tokenService.saveRefreshToken(refreshToken, userDetails.getUsername());

                    return Mono.just(ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken)));
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).body(new JwtAuthenticationResponse(e.getMessage()))));
    }

}
