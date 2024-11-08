package com.cruz_sur.api.security;

import com.cruz_sur.api.jwt.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configuración CORS global
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll() // Rutas permitidas sin autenticación
                        .requestMatchers("/api/v1/campos/available-sedes").permitAll()
                        .requestMatchers("/api/v1/departamento").permitAll()
                        .requestMatchers("/api/v1/distrito").permitAll()
                        .requestMatchers("/api/v1/provincia").permitAll()
                        .requestMatchers("/api/v1/campos//usuario/{usuarioId}/with-sede").permitAll()
                        .requestMatchers("/**").hasAnyRole("USER", "ADMIN") // Rutas protegidas
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // CORS específico para las rutas permitidas
    @Bean
    public CorsConfigurationSource permitAllCorsConfigurationSource() {
        CorsConfiguration permitAllConfiguration = new CorsConfiguration();
        permitAllConfiguration.setAllowedOrigins(List.of("http://localhost:4200","https://zemply.vercel.app"));
        permitAllConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "OPTIONS"));
        permitAllConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        permitAllConfiguration.setAllowCredentials(true); // Permitir credenciales

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Registra el CORS solo para las rutas que están permitidas sin autenticación
        source.registerCorsConfiguration("/auth/**", permitAllConfiguration);
        source.registerCorsConfiguration("/api/v1/campos/available-sedes", permitAllConfiguration);
        source.registerCorsConfiguration("/api/v1/departamento", permitAllConfiguration);
        source.registerCorsConfiguration("/api/v1/distrito", permitAllConfiguration);
        source.registerCorsConfiguration("/api/v1/provincia", permitAllConfiguration);
        source.registerCorsConfiguration("/api/v1/campos//usuario/{usuarioId}/with-sede", permitAllConfiguration);

        return source;
    }
}
