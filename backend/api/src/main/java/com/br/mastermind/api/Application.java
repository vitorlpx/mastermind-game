package com.br.mastermind.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
			.filename(".env")
			.ignoreIfMissing()
			.load();

		setProperty("DB_URL", dotenv);
		setProperty("DB_NAME", dotenv);
		setProperty("DB_USER", dotenv);
		setProperty("DB_PASSWORD", dotenv);
		setProperty("JWT_SECRET", dotenv);
		setProperty("JWT_EXPIRATION", dotenv);

		SpringApplication.run(Application.class, args);
	}

	private static void setProperty(String key, Dotenv dotenv) {
		// Tenta pegar do .env primeiro, senão pega do ambiente do sistema (Docker)
		String value = dotenv.get(key);
		if (value == null) {
			value = System.getenv(key);
		}
		if (value != null) {
			System.setProperty(key, value);
		}
	}

}
