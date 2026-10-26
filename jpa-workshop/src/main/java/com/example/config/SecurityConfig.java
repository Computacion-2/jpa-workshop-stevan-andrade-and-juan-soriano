package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                        "/mvc/auth/login",
                        "/mvc/auth/register",
                        "/error",
                        "/css/**"
                    ).permitAll()
                    .anyRequest().authenticated())

                .formLogin(form -> form
                    .loginPage("/mvc/auth/login")
                    .loginProcessingUrl("/mvc/auth/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/mvc/auth/login?error=true")
                    .permitAll())

                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/mvc/auth/login?logout=true")
                    .invalidateHttpSession(true)
                    .permitAll())

                .exceptionHandling(exception -> exception
                    .accessDeniedPage("/access-denied"))

                // la API REST del taller 2 y la consola de H2 no usan formularios de Thymeleaf
                .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/api/**", "/h2-console/**"))
                .headers(headers -> headers
                    .frameOptions(frame -> frame.sameOrigin()))

                .build();
    }
}
