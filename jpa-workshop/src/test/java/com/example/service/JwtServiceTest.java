package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.model.Permission;
import com.example.model.Role;
import com.example.model.User;
import com.example.security.AppUserDetails;
import com.example.service.impl.JwtService;

public class JwtServiceTest {

    private static final String SECRET = "b/V4XY88GUoxRAm9boa8/T3PljWs8KeSxhh3BW7E0DI=";
    private static final String OTHER_SECRET = "c2VjcmV0by1kaWZlcmVudGUtcGFyYS1sYXMtcHJ1ZWJhcy0xMjM0NTY=";

    private JwtService jwtService;
    private AppUserDetails admin;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService(SECRET, 3600000);

        Permission userRead = new Permission();
        userRead.setId(1L);
        userRead.setName("USER_READ");

        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.addPermission(userRead);

        User user = new User();
        user.setId(1L);
        user.setName("Administrador");
        user.setEmail("admin@finanzas.com");
        user.setPassword("$2a$10$hash");
        user.setRole(role);

        admin = new AppUserDetails(user);
    }

    @Test
    public void testGeneratedTokenIsValid() {
        String token = jwtService.generateToken(admin);

        assertTrue(jwtService.isTokenValid(token));
        assertEquals("admin@finanzas.com", jwtService.extractUsername(token));
    }

    @Test
    public void testTokenKeepsRoleAndPermissions() {
        String token = jwtService.generateToken(admin);

        UserDetails details = jwtService.getUserDetailsFromToken(token);
        Set<String> authorities = details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertEquals("admin@finanzas.com", details.getUsername());
        assertEquals(Set.of("ROLE_ADMIN", "USER_READ"), authorities);
    }

    @Test
    public void testTamperedTokenIsInvalid() {
        String token = jwtService.generateToken(admin);
        int signatureStart = token.lastIndexOf('.') + 1;
        char first = token.charAt(signatureStart);
        String tampered = token.substring(0, signatureStart) + (first == 'A' ? 'B' : 'A')
                + token.substring(signatureStart + 1);

        assertFalse(jwtService.isTokenValid(tampered));
    }

    @Test
    public void testTokenSignedWithOtherKeyIsInvalid() {
        String token = new JwtService(OTHER_SECRET, 3600000).generateToken(admin);

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    public void testExpiredTokenIsInvalid() {
        String token = new JwtService(SECRET, -1000).generateToken(admin);

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    public void testGarbageIsInvalid() {
        assertFalse(jwtService.isTokenValid("esto-no-es-un-token"));
        assertFalse(jwtService.isTokenValid(""));
    }
}
