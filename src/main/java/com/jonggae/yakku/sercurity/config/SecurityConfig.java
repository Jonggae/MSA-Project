package com.jonggae.yakku.sercurity.config;

import com.jonggae.yakku.sercurity.handler.jwt.JwtAccessDeniedHandler;
import com.jonggae.yakku.sercurity.handler.jwt.JwtAuthenticationEntryPoint;
import com.jonggae.yakku.sercurity.handler.login.LoginFailureHandler;
import com.jonggae.yakku.sercurity.handler.login.LoginSuccessHandler;
import com.jonggae.yakku.sercurity.jwt.JwtAuthenticationFilter;
import com.jonggae.yakku.sercurity.jwt.JwtFilter;
import com.jonggae.yakku.sercurity.jwt.LoginProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final LoginProvider loginProvider;
    private final LoginSuccessHandler successHandler;
    private final LoginFailureHandler failureHandler;
    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return loginProvider;
    }

    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(loginProvider, successHandler, failureHandler);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {

        http.authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                .requestMatchers("/api/customers/login").permitAll()
                .requestMatchers("/api/customers/register").permitAll()
                .requestMatchers("/api/customers/confirm").permitAll()
                .anyRequest().authenticated());

        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint));

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable); // CSRF 보호 비활성화 (필요에 따라 활성화)
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); //

        return http.build();
    }
}
