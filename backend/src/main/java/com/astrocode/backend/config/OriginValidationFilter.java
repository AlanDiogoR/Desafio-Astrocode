package com.astrocode.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Mitigação CSRF para auth por cookie (SameSite=None): exige Origin/Referer
 * em requisições state-changing vindas do browser. Complementa CORS.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class OriginValidationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(OriginValidationFilter.class);
    private static final List<String> STATE_CHANGING_METHODS = List.of("POST", "PUT", "PATCH", "DELETE");

    @Value("${app.cors.allowed-origins:}")
    private String allowedOriginsConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!STATE_CHANGING_METHODS.contains(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        if ((origin == null || origin.isBlank()) && (referer == null || referer.isBlank())) {
            log.warn("Requisição state-changing sem Origin/Referer - possível CSRF ou cliente non-browser");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Origin ou Referer obrigatório");
            return;
        }

        String originToCheck = origin != null && !origin.isBlank() ? origin : extractOriginFromReferer(referer);
        if (originToCheck != null && !isAllowedOrigin(originToCheck)) {
            log.warn("Origem não permitida: {}", originToCheck);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Origem não permitida");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractOriginFromReferer(String referer) {
        if (referer == null || referer.isBlank()) return null;
        int idx = referer.indexOf("://");
        if (idx < 0) return null;
        int pathStart = referer.indexOf("/", idx + 3);
        return pathStart > 0 ? referer.substring(0, pathStart) : referer;
    }

    private boolean isAllowedOrigin(String origin) {
        List<String> allowed = getAllowedOriginsList();
        return allowed.stream().anyMatch(o -> origin.equals(o));
    }

    private List<String> getAllowedOriginsList() {
        if (allowedOriginsConfig != null && !allowedOriginsConfig.isBlank()) {
            return Arrays.stream(allowedOriginsConfig.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
        log.warn("app.cors.allowed-origins nao configurado, usando fallback de desenvolvimento");
        return List.of(
                "https://grivy.netlify.app",
                "https://www.grivy.netlify.app",
                "http://localhost:3000",
                "http://localhost:5173"
        );
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/actuator/") || path.startsWith("/api/webhooks/") || path.startsWith("/swagger")) {
            return true;
        }
        // Mercado Pago envia POST sem Origin/Referer — não bloquear o webhook de assinaturas
        if (path.startsWith("/api/subscriptions/webhook")) {
            return true;
        }
        // Login/refresh podem ser chamados sem Origin (CLI, ferramentas, integrações)
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/refresh")) {
            return true;
        }
        return false;
    }
}
