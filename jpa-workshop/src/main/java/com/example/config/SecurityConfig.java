package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.security.JwtAuthenticationFilter;
import com.example.security.RestSecurityErrorHandler;
import com.example.service.interf.IJwtService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, IJwtService jwtService) throws Exception {
        RestSecurityErrorHandler errorHandler = new RestSecurityErrorHandler();
        return http
                .securityMatcher("/api/**")

                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()

                    .requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority("USER_READ")
                    .requestMatchers("/api/users/**").hasAuthority("USER_WRITE")

                    .requestMatchers(HttpMethod.DELETE, "/api/roles/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/roles/**").hasAuthority("ROLE_READ")
                    .requestMatchers("/api/roles/**").hasAuthority("ROLE_WRITE")

                    .requestMatchers(HttpMethod.GET, "/api/permissions/**").hasAuthority("PERMISSION_READ")
                    .requestMatchers("/api/permissions/**").hasAuthority("PERMISSION_WRITE")

                    .anyRequest().authenticated())

                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(errorHandler)
                    .accessDeniedHandler(errorHandler))

                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    @Order(2)
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

                // la consola de H2 no usa formularios de Thymeleaf
                .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers
                    .frameOptions(frame -> frame.sameOrigin()))

                .build();
    }
}
