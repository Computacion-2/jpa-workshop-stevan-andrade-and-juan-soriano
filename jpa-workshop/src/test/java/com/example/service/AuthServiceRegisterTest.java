package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.dto.RoleResponseDTO;
import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.repository.UserRepository;
import com.example.service.impl.AuthService;
import com.example.service.impl.UserService;
import com.example.service.interf.IJwtService;
import com.example.service.interf.IRoleService;
import com.example.service.interf.IUserService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceRegisterTest {

    @Mock private IUserService userService;
    @Mock private IRoleService roleService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserDetailsService userDetailsService;
    @Mock private IJwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private UserRequestDTO request(String password) {
        return new UserRequestDTO("Ana Gómez", "ana@finanzas.com", password, null);
    }

    @Test
    public void testRegisterAssignsDefaultRole() {
        when(userRepository.existsByEmail("ana@finanzas.com")).thenReturn(false);
        when(roleService.findByName(UserService.DEFAULT_ROLE)).thenReturn(new RoleResponseDTO(2L, "USER"));
        when(userService.create(any(UserRequestDTO.class)))
                .thenReturn(new UserResponseDTO(5L, "Ana Gómez", "ana@finanzas.com"));

        UserResponseDTO result = authService.register(request("clave123"), "clave123");

        ArgumentCaptor<UserRequestDTO> sent = ArgumentCaptor.forClass(UserRequestDTO.class);
        verify(userService, times(1)).create(sent.capture());
        assertEquals(2L, sent.getValue().getRoleId());
        assertEquals(5L, result.getId());
    }

    @Test
    public void testRegisterDoesNotHashTwice() {
        when(userRepository.existsByEmail("ana@finanzas.com")).thenReturn(false);
        when(roleService.findByName(UserService.DEFAULT_ROLE)).thenReturn(new RoleResponseDTO(2L, "USER"));

        authService.register(request("clave123"), "clave123");

        ArgumentCaptor<UserRequestDTO> sent = ArgumentCaptor.forClass(UserRequestDTO.class);
        verify(userService).create(sent.capture());
        assertEquals("clave123", sent.getValue().getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    public void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail("ana@finanzas.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> authService.register(request("clave123"), "clave123"));
        verify(userService, never()).create(any());
    }

    @Test
    public void testRegisterPasswordsDoNotMatch() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.register(request("clave123"), "otraclave"));
        verify(userService, never()).create(any());
    }

    @Test
    public void testRegisterShortPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.register(request("123"), "123"));
        verify(userService, never()).create(any());
    }

    @Test
    public void testRegisterMissingData() {
        UserRequestDTO incomplete = new UserRequestDTO("", "ana@finanzas.com", "clave123", null);

        assertThrows(IllegalArgumentException.class, () -> authService.register(incomplete, "clave123"));
        verify(userService, never()).create(any());
    }
}
