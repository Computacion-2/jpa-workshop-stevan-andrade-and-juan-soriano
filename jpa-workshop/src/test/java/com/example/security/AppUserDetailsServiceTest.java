package com.example.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.model.Permission;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService userDetailsService;

    private User user;

    @BeforeEach
    public void setUp() {
        Permission userRead = new Permission();
        userRead.setId(1L);
        userRead.setName("USER_READ");

        Permission roleWrite = new Permission();
        roleWrite.setId(4L);
        roleWrite.setName("ROLE_WRITE");

        Role admin = new Role();
        admin.setId(1L);
        admin.setName("ADMIN");
        admin.addPermission(userRead);
        admin.addPermission(roleWrite);

        user = new User();
        user.setId(1L);
        user.setName("Administrador");
        user.setEmail("admin@finanzas.com");
        user.setPassword("$2a$10$hash");
        user.setRole(admin);
    }

    @Test
    public void testLoadUserByUsername() {
        when(userRepository.findByEmail("admin@finanzas.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("admin@finanzas.com");

        assertEquals("admin@finanzas.com", details.getUsername());
        assertEquals("$2a$10$hash", details.getPassword());
    }

    @Test
    public void testAuthoritiesHaveRoleAndPermissions() {
        when(userRepository.findByEmail("admin@finanzas.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("admin@finanzas.com");
        Set<String> authorities = details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertEquals(Set.of("ROLE_ADMIN", "USER_READ", "ROLE_WRITE"), authorities);
    }

    @Test
    public void testAppUserDetailsExposesNameRoleAndPermissions() {
        when(userRepository.findByEmail("admin@finanzas.com")).thenReturn(Optional.of(user));

        AppUserDetails details = (AppUserDetails) userDetailsService.loadUserByUsername("admin@finanzas.com");

        assertEquals("Administrador", details.getName());
        assertEquals("ADMIN", details.getRoleName());
        assertEquals(java.util.List.of("ROLE_WRITE", "USER_READ"), details.getPermissions());
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail("nadie@finanzas.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nadie@finanzas.com"));
    }
}
