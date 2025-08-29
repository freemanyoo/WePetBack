package com.busanit501.findmyfet.security;


import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher; // ❗ AntPathMatcher import
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    // ❗ AntPathMatcher 인스턴스를 필드로 추가합니다.
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter: doFilterInternal called for URI: {}", request.getRequestURI());

        // ❗❗❗ [수정된 부분] 보안 예외 경로를 먼저 확인합니다. ❗❗❗
        String requestURI = request.getRequestURI();
        String[] ignoredPaths = {"/upload/**", "/api/auth/**"};

        for (String path : ignoredPaths) {
            if (antPathMatcher.match(path, requestURI)) {
                log.info("JwtAuthenticationFilter: Ignoring path: {}", requestURI);
                filterChain.doFilter(request, response); // 토큰 검사 없이 다음 필터로 진행
                return; // 필터 로직 종료
            }
        }
        // ❗❗❗ [수정된 부분 끝] ❗❗❗

        // 아래는 기존의 토큰 추출 및 검증 로직입니다.
        String token = extractToken(request);

        if (StringUtils.hasText(token)) {
            log.info("JwtAuthenticationFilter: Token found: {}", token);
            try {
                Claims claims = jwtUtil.validateToken(token);
                String loginId = claims.getSubject();
                log.info("JwtAuthenticationFilter: Token validated for loginId: {}", loginId);

                User user = userRepository.findByLoginId(loginId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                log.info("JwtAuthenticationFilter: User found: {}", user.getLoginId());

                UserDetailsImpl userDetails = new UserDetailsImpl(user);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                log.info("JwtAuthenticationFilter: Authorities: {}", authentication.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JwtAuthenticationFilter: SecurityContextHolder updated.");

            } catch (Exception e) {
                log.error("JwtAuthenticationFilter: Token validation failed or user not found: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            log.info("JwtAuthenticationFilter: No token found in request.");
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}