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
