
package com.projet.auto_battler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Provides password hashing for account registration and login.
 */
@Configuration
public class SecurityConfig {

    /**
     * Creates the BCrypt password encoder used by {@code PlayerService}.
     *
     * @return the configured password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
