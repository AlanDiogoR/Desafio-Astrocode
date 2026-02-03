package com.astrocode.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		// Tenta encontrar o arquivo .env em diferentes locais
		Dotenv dotenv = null;
		String[] possiblePaths = {
			"./.env",                    // Diretório atual (quando executado de backend/)
			"../backend/.env",           // Um nível acima (quando executado da raiz)
			"./backend/.env"            // Subdiretório backend
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
					// Tenta próximo caminho
				}
			}
		}
		
		// Se não encontrou em nenhum lugar, tenta carregar do diretório atual
		if (dotenv == null) {
			dotenv = Dotenv.configure()
					.directory(".")
					.filename(".env")
					.ignoreIfMissing()
					.load();
		}
		
		// Define as variáveis como propriedades do sistema ANTES do Spring Boot iniciar
		if (dotenv != null) {
			dotenv.entries().forEach(entry -> {
				String key = entry.getKey();
				String value = entry.getValue();
				System.setProperty(key, value);
				// Log para debug
				if (!key.contains("PASSWORD")) {
					System.out.println("Loaded: " + key + " = " + value);
				} else {
					System.out.println("Loaded: " + key + " = ***");
				}
			});
		} else {
			System.err.println("WARNING: Arquivo .env não encontrado! Usando variáveis de ambiente do sistema.");
		}
		
		SpringApplication.run(BackendApplication.class, args);
	}

}
