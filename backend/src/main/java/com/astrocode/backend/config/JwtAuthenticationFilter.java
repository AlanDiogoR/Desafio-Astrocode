package com.astrocode.backend.config;

import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.repositories.UserRepository;
import com.astrocode.backend.domain.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = authHeader.substring(BEARER_PREFIX.length());

        try {
            if (jwtService.isTokenValid(token)) {
                var email = jwtService.extractEmail(token);
                var userId = jwtService.extractUserId(token);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    var userOpt = userRepository.findById(userId);

                    if (userOpt.isPresent()) {
                        var user = userOpt.get();
                        
                        var authToken = new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                null
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    if (email == null) {
                        SecurityContextHolder.clearContext();
                    }
                }
            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
