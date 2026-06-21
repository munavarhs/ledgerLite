package com.example.demo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Pull the Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. No "Bearer <token>"? Skip auth, let the request continue.
        //    (It'll get rejected later if the route needs auth.)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Strip "Bearer " prefix to get the raw token
        String token = authHeader.substring(7);

        // 4. Validate it and, if good, mark the user authenticated
        if (jwtService.isTokenValid(token)) {
            String username = jwtService.extractUsername(token);

            var authentication = new UsernamePasswordAuthenticationToken(
                    username, null, Collections.emptyList());
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            // This line is the whole point: tell Spring "this request is authenticated"
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. Hand off to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}