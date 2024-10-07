package com.cruz_sur.api.security;

import com.cruz_sur.api.jwt.JwtFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/api/v1/auth/login").permitAll() // Rutas de autenticación
                            .requestMatchers("/public/**").permitAll() // Rutas públicas
                            .requestMatchers("/api/v1/**").hasAnyRole("USER")
                            .requestMatchers("/api/v1/ale/**").hasAnyRole( "ADMIN") // Rutas protegidas
// Rutas protegidas
                            .anyRequest().authenticated();
                })
                .oauth2Login(Customizer.withDefaults()) // Login OAuth2
                .formLogin(Customizer.withDefaults()) // Login de formulario
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Filtro JWT
                .build();
    }
}
