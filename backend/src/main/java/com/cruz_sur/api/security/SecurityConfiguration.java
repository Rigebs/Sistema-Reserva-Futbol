package com.cruz_sur.api.security;

import com.cruz_sur.api.jwt.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/v1/campos/available-sedes").permitAll()
                        .requestMatchers("/api/v1/departamento/**").permitAll()
                        .requestMatchers("/api/v1/distrito/**").permitAll()
                        .requestMatchers("/api/v1/opiniones/compania/{companiaId}").permitAll()
                        .requestMatchers("/api/v1/opiniones/resumen/{companiaId}").permitAll()
                        .requestMatchers("/api/v1/provincia/**").permitAll()
                        .requestMatchers("/api/v1/availability/**").permitAll()
                        .requestMatchers("/api/v1/campos/usuario/{usuarioId}/with-sede").permitAll()
                        .requestMatchers("/auth/oauth2/success").hasAnyAuthority("SCOPE_openid")
                        .requestMatchers("/**").hasAnyRole("USER", "ADMIN","CLIENTE", "ESPERA","COMPANIA")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/auth/oauth2/success", true) // Redirige aquí después de autenticación exitosa
                )

                .addFilterBefore(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200","https://zemply.netlify.app","https://zemply.onrender.com","https://zemply.vercel.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "OPTIONS","DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}