package com.astrocode.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableScheduling
public class BackendApplication {

	public static void main(String[] args) {
		// Tenta encontrar o arquivo .env em diferentes locais
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
					System.out.println("Loaded .env from: " + envFile.getAbsolutePath());
					break;
				} catch (Exception e) {
					// Continua tentando outros caminhos
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
			System.out.println("Loaded .env: " + count + " variables (values nunca logados por segurança)");
		} else {
			System.err.println("WARNING: Arquivo .env não encontrado! Usando variáveis de ambiente do sistema.");
		}

		// Diagnóstico JWT_SECRET (nunca logar o valor)
		String jwtSecret = System.getProperty("JWT_SECRET", System.getenv("JWT_SECRET"));
		if (jwtSecret != null && !jwtSecret.isBlank()) {
			System.out.println("JWT_SECRET: presente, " + jwtSecret.trim().length() + " chars");
		} else {
			System.err.println("CRITICO: JWT_SECRET não encontrado nas variáveis de ambiente!");
		}

		SpringApplication.run(BackendApplication.class, args);
	}

}
