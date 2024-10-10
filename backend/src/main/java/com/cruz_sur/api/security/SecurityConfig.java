package com.cruz_sur.api.security;

import com.cruz_sur.api.jwt.JwtFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/auth/login").permitAll()
                            .requestMatchers("/privado/**").hasAuthority("SCOPE_openid")
                            .requestMatchers("/api/v1/**").hasAnyRole("USER","ADMIN")
                            .requestMatchers("/api/v1/**").hasRole("ADMIN")
                            .anyRequest().authenticated();
                })
                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/api/v1/auth/login");
                        })
                        .failureUrl("/api/v1/auth/login?error=true")
                )
                .formLogin(form -> form
                        .successHandler(successHandler())
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handling -> handling
                        .accessDeniedPage("/api/v1/auth/login"))
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler();
    }
}
