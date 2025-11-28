package org.banking.demo.bankingcustomer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationConverterKeyCloak jwtAuthenticationConverterKeyCloak;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        return httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        http -> http.anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> {
                    oauth.jwt(
                            jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverterKeyCloak)
                    );
                })
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .build();
    }
}
