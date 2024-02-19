package com.vulturi.trading.api.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    String[] whitelist = {
            // -- swagger ui
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            //other public endpoints may be appended to this array
            "/actuator/health",
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> {
                    authz
                            .requestMatchers(HttpMethod.POST, "/*/user/login").permitAll()
                            .requestMatchers(HttpMethod.POST, "/*/user/otp").permitAll()
                            .requestMatchers(HttpMethod.GET, "/*/user/me").permitAll()
                            .requestMatchers(whitelist).permitAll()
                            .anyRequest().authenticated();
                })
                .oauth2ResourceServer().jwt();
        return http.build();
    }



}