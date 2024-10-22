package org.example.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers("/register").permitAll()  // Allow unauthenticated users to access /register
                .requestMatchers("/login").permitAll()  // Allow all users to access the login page
                .anyRequest().permitAll();
        return http.build();
    }
}
