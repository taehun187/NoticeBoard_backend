package com.taehun.board.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.PathContainer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final List<String> excludePaths;
    private final PathPatternParser pathPatternParser = new PathPatternParser();

    public JwtAuthenticationFilter(TokenProvider tokenProvider, List<String> excludePaths) {
        this.tokenProvider = tokenProvider;
        this.excludePaths = excludePaths;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean isOptionsRequest = "OPTIONS".equalsIgnoreCase(request.getMethod()); // OPTIONS 요청 확인
        boolean shouldExclude = excludePaths.stream().anyMatch(path::startsWith); // 제외 경로 확인

        log.debug("Request URI: {}, Is OPTIONS Request: {}, Should Exclude: {}", path, isOptionsRequest, shouldExclude);

        // OPTIONS 요청이거나 제외 경로에 해당하면 필터링하지 않음
        return isOptionsRequest || shouldExclude;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws java.io.IOException, jakarta.servlet.ServletException {
        String requestUri = request.getRequestURI();
        log.debug("doFilterInternal called for URI: {}", requestUri);

        if (shouldNotFilter(request)) {
            log.debug("Skipping filter for URI: {}", requestUri);
            chain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        try {
            if (token != null && tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromToken(token);
                PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
                        username, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.debug("Invalid or missing JWT token for URI: {}", requestUri);
            }
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token for URI: {}, token={}", requestUri, token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Token expired. Please re-login.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.debug("Authorization Header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
