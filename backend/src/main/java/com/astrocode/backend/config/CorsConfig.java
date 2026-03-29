package com.astrocode.backend.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CORS na cadeia Servlet (roda ANTES do Spring Security e DispatcherServlet).
 * Garante headers CORS em TODAS as respostas, inclusive preflight OPTIONS, 401, 502 e erros.
 * Quando a app responde, o Railway repassa; sem este filtro, erros podem não ter CORS.
 * <p>
 * Railway: configure APP_CORS_ORIGINS sem aspas, ex: https://grivy.netlify.app
 * Múltiplas origens: separar por vírgula, sem espaços extras.
 */
@Configuration
public class CorsConfig {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${app.cors.allowed-origins:}")
    private String allowedOriginsConfig;

    @PostConstruct
    public void validateConfig() {
        if (allowedOriginsConfig == null || allowedOriginsConfig.isBlank()) {
            throw new IllegalStateException(
                    "APP_CORS_ORIGINS não está configurado. "
                            + "Defina a variável de ambiente com a URL do frontend (ex: https://grivy.netlify.app)"
            );
        }
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        List<String> origins = getAllowedOrigins();
        log.info("CORS configured with origins: {}", origins);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration(origins));

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    private CorsConfiguration corsConfiguration(List<String> origins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "Accept",
                "X-Requested-With", "Origin", "Cache-Control", "X-Request-Id",
                "If-None-Match", "If-Modified-Since", "Pragma"
        ));
        config.setExposedHeaders(List.of("Set-Cookie", "X-Request-Id"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        return config;
    }

    private List<String> getAllowedOrigins() {
        return Arrays.stream(allowedOriginsConfig.split(","))
                .map(String::trim)
                .map(s -> s.replaceAll("/$", ""))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
