package com.projet.auto_battler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstraps the Auto-Battler Spring Boot application.
 */
@SpringBootApplication
public class AutoBattlerApplication {

	/**
	 * Starts the embedded application.
	 *
	 * @param args command-line arguments passed to Spring Boot
	 */
	public static void main(String[] args) {
		SpringApplication.run(AutoBattlerApplication.class, args);
	}

}
