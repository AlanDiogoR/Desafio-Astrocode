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
import java.util.Optional;
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
        System.out.println("[JwtFilter] Token encontrado no header");

        try {
            if (jwtService.isTokenValid(token)) {
                System.out.println("[JwtFilter] Token válido");
                
                var email = jwtService.extractEmail(token);
                var userId = jwtService.extractUserId(token);
                System.out.println("[JwtFilter] Email extraído: " + email);
                System.out.println("[JwtFilter] UserId extraído: " + userId);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    System.out.println("[JwtFilter] SecurityContext vazio, buscando usuário...");
                    
                    var userOpt = userRepository.findById(userId);

                    if (userOpt.isPresent()) {
                        var user = userOpt.get();
                        System.out.println("[JwtFilter] Usuário encontrado: " + user.getEmail());
                        
                        var authToken = new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                null
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("[JwtFilter] Authentication configurado no SecurityContext");
                    } else {
                        System.out.println("[JwtFilter] Usuário não encontrado no banco");
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    if (email == null) {
                        System.out.println("[JwtFilter] Email é nulo, limpando contexto");
                        SecurityContextHolder.clearContext();
                    } else {
                        System.out.println("[JwtFilter] Authentication já existe no contexto");
                    }
                }
            } else {
                System.out.println("[JwtFilter] Token inválido");
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            System.out.println("[JwtFilter] Erro ao processar token: " + e.getMessage());
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
