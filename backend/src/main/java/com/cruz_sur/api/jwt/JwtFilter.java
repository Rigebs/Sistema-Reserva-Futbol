package com.cruz_sur.api.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final JwtInvalidTokenService invalidTokenService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, JwtInvalidTokenService invalidTokenService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.invalidTokenService = invalidTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String mail = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            mail = jwtUtil.getUsername(token);
        }

        if (token != null && invalidTokenService.isTokenInvalid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (mail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(mail);

            if (jwtUtil.validateToken(token)) {
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(userDetails));
            }
        }

        filterChain.doFilter(request, response);
    }
}
