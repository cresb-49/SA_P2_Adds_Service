package com.sap.adds_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("local") // <- solo se activa con el perfil 'dev'
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())            // no CSRF (APIs stateless)
                .cors(Customizer.withDefaults())         // habilita CORS (usará el bean CorsConfigurationSource si existe)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()              // TODO: volver a restringir en prod
                );

        return http.build();
    }
}