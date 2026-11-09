package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.dto.LoginRequestDTO;
import com.example.dto.TokenResponseDTO;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.AppUserDetails;
import com.example.service.impl.AuthService;
import com.example.service.interf.IJwtService;
import com.example.service.interf.IRoleService;
import com.example.service.interf.IUserService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceLoginTest {

    @Mock private IUserService userService;
    @Mock private IRoleService roleService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserDetailsService userDetailsService;
    @Mock private IJwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private AppUserDetails ana;

    @BeforeEach
    public void setUp() {
        Role role = new Role();
        role.setId(2L);
        role.setName("USER");

        User user = new User();
        user.setId(2L);
        user.setName("Ana Gómez");
        user.setEmail("ana@finanzas.com");
        user.setPassword("$2a$10$hash");
        user.setRole(role);

        ana = new AppUserDetails(user);
    }

    @Test
    public void testLoginReturnsToken() {
        when(userDetailsService.loadUserByUsername("ana@finanzas.com")).thenReturn(ana);
        when(passwordEncoder.matches("Password123*", "$2a$10$hash")).thenReturn(true);
        when(jwtService.generateToken(ana)).thenReturn("token.jwt.firmado");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        TokenResponseDTO response = authService.login(new LoginRequestDTO("ana@finanzas.com", "Password123*"));

        assertEquals("token.jwt.firmado", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600, response.getExpiresIn());
    }

    @Test
    public void testLoginWrongPasswordThrowsBadCredentials() {
        when(userDetailsService.loadUserByUsername("ana@finanzas.com")).thenReturn(ana);
        when(passwordEncoder.matches("otra", "$2a$10$hash")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequestDTO("ana@finanzas.com", "otra")));
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    public void testLoginUnknownEmailThrowsBadCredentials() {
        when(userDetailsService.loadUserByUsername("nadie@finanzas.com"))
                .thenThrow(new UsernameNotFoundException("no existe"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequestDTO("nadie@finanzas.com", "Password123*")));
    }

    @Test
    public void testLoginWithoutDataThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.login(new LoginRequestDTO("", "")));
    }
}
