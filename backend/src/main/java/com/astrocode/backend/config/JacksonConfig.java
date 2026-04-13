package com.astrocode.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Spring Boot 4 não expõe mais automaticamente um bean {@link ObjectMapper} (foco em Jackson 3 / {@code JsonMapper}).
 * Componentes que ainda usam {@code com.fasterxml.jackson.databind.ObjectMapper} precisam de um bean explícito.
 */
@Configuration
public class JacksonConfig {

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
