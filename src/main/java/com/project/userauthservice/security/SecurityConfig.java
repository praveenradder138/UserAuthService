package com.project.userauthservice.security;

import com.project.userauthservice.security.filters.JwtAuthenticationFilter;
import com.project.userauthservice.security.service.CustomUserDetailsService;
import com.project.userauthservice.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()).authorizeRequests(authorize -> authorize
                .requestMatchers("/users/signup","/users/signin", "/users/verifyEmail", "/users/verificationEmail").permitAll()
                .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtils, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}