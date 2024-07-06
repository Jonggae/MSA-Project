package com.jonggae.yakku.sercurity.jwt;

import com.jonggae.yakku.sercurity.utils.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        logger.debug("Processing request to: {}", requestURI);
// todo: 코드가 지저분하니 정리해봅시다

        if (!requestURI.startsWith("/api/customers/register")) {
            try {
                if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                    Authentication authentication = tokenProvider.getAuthentication(jwt);
                    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
                    } else {
                        logger.debug("유효한 인증정보를 가져오지 못했습니다, uri: {}", requestURI);
                    }
                } else {
                    logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
                }
            } catch (Exception e) {
                logger.error("JWT 처리 중 오류 발생: {}", e.getMessage());
            }
        } else {
            logger.debug("Skipping JWT filter for registration endpoint: {}", requestURI);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }


    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
