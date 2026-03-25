package com.astrocode.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableScheduling
public class BackendApplication {

	private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

	public static void main(String[] args) {
		Dotenv dotenv = null;
		String[] possiblePaths = {
				"./.env",
				"../backend/.env",
				"./backend/.env"
		};

		for (String path : possiblePaths) {
			File envFile = new File(path);
			if (envFile.exists()) {
				try {
					dotenv = Dotenv.configure()
							.directory(envFile.getParent())
							.filename(".env")
							.load();
					log.info("Arquivo .env carregado de: {}", envFile.getAbsolutePath());
					break;
				} catch (Exception e) {
					log.trace("Ignorando .env em {}: {}", path, e.toString());
				}
			}
		}

		if (dotenv == null) {
			dotenv = Dotenv.configure()
					.directory(".")
					.filename(".env")
					.ignoreIfMissing()
					.load();
		}

		if (dotenv != null) {
			int count = 0;
			for (var entry : dotenv.entries()) {
				System.setProperty(entry.getKey(), entry.getValue());
				count++;
			}
			log.info("Variáveis do .env aplicadas ao System: {} entradas (valores não logados)", count);
		} else {
			log.warn("Arquivo .env não encontrado; usando apenas variáveis de ambiente do sistema");
		}

		String jwtSecret = System.getProperty("JWT_SECRET", System.getenv("JWT_SECRET"));
		if (jwtSecret != null && !jwtSecret.isBlank()) {
			log.info("JWT_SECRET definido (comprimento {} caracteres)", jwtSecret.trim().length());
		} else {
			log.error("JWT_SECRET ausente: defina em ambiente ou .env");
		}

		SpringApplication.run(BackendApplication.class, args);
	}

}
