package com.example.responselink.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }

    // Create Users
    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        UserDetails coordinator = User.builder()
                .username("coordinator")
                .password(passwordEncoder.encode(
                        System.getenv("COORDINATOR_PASSWORD") == null
                                ? "Coordinator@123"
                                : System.getenv("COORDINATOR_PASSWORD")
                ))
                .roles("COORDINATOR")
                .build();

        UserDetails viewer = User.builder()
                .username("viewer")
                .password(passwordEncoder.encode(
                        System.getenv("VIEWER_PASSWORD") == null
                                ? "Viewer@123"
                                : System.getenv("VIEWER_PASSWORD")
                ))
                .roles("VIEWER")
                .build();

        return new InMemoryUserDetailsManager(
                coordinator,
                viewer
        );
    }

    // Security Rules
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Coordinator only
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/incidents",
                                "/api/responders",
                                "/api/incidents/*/responders"
                        ).hasRole("COORDINATOR")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PATCH,
                                "/api/incidents/*/status"
                        ).hasRole("COORDINATOR")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/incidents/*/responders"
                        ).hasRole("COORDINATOR")

                        // Viewer and Coordinator can read
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/**"
                        ).hasAnyRole("COORDINATOR", "VIEWER")

                        .anyRequest().authenticated()
                )

                .httpBasic(basic -> {});

        return http.build();
    }
}