package com.project.userauthservice.security;

import com.project.userauthservice.security.filters.JwtAuthenticationFilter;
import com.project.userauthservice.security.service.CustomUserDetailsService;
import com.project.userauthservice.services.UserService;
import com.project.userauthservice.utils.Constants;
import com.project.userauthservice.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserService userService;

    public SecurityConfig(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService,
                          UserService userService ) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
        this.userService=userService;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeRequests(authorize -> authorize
                .requestMatchers("/signup","/signin", "/verifyEmail", "/verificationEmail","/oauth2/**","/favicon.ico").permitAll()
                .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2Login(
                        oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                            OAuth2User oauth2User = oauthToken.getPrincipal();
                            String name = oauth2User.getAttribute("name");
                            String email = oauth2User.getAttribute("email");
                            String providerId = oauth2User.getAttribute("sub");
                            String provider=oauthToken.getAuthorizedClientRegistrationId();
                            String jwtToken = userService.saveUserAndGetOAuthToken(name, email, provider, providerId);
                            Cookie cookie = setCookie(jwtToken);
                            addResponseHeader(response, jwtToken, cookie);
                            response.sendRedirect("/validateToken");
                        })
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtils, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static void addResponseHeader(HttpServletResponse response, String jwtToken, Cookie cookie)  {
        response.setHeader(Constants.AUTHORIZATION, Constants.BEARER_TOKEN + jwtToken);
        response.addCookie(cookie);
    }

    private static Cookie setCookie(String jwtToken) {
        Cookie cookie = new Cookie(Constants.AUTHORIZATION, jwtToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}