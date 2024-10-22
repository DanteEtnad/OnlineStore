package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/store/register").permitAll() // 注册请求无需身份验证
                        .anyRequest().permitAll() // 其他所有请求不需要身份验证
                )
                .csrf(csrf -> csrf.disable()); // 禁用 CSRF 保护（仅在确实需要时使用）

        return http.build();
    }

}
