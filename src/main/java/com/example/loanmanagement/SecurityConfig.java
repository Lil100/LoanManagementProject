package com.example.loanmanagement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/customer/customers").permitAll()  // Allow public access for customer CRUD operations
                        .requestMatchers("/api/customer/{id}").permitAll()
                        .requestMatchers("/api/loan/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/loan/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/loan/**").permitAll()

                        .requestMatchers("/api/user/**").permitAll()
                        .requestMatchers("/api/statistics/**").permitAll()
                        .requestMatchers("/api/repayment-schedule/**").permitAll()

                        .anyRequest().authenticated()  // Protect all other endpoints
                )
                .formLogin().disable() // Disable default login form
                .httpBasic(); // Enable basic authentication

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
