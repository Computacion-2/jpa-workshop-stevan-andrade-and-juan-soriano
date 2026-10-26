package com.example.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.model.User;
import com.example.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class DatabaseInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DatabaseInitializer databaseInitializer;

    private User user(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    @Test
    public void testHashesPendingPasswords() {
        User admin = user("admin@finanzas.com", "PENDING_HASH");
        when(userRepository.findAll()).thenReturn(List.of(admin));
        when(passwordEncoder.encode("admin123")).thenReturn("HASH_ADMIN");

        databaseInitializer.run(null);

        assertEquals("HASH_ADMIN", admin.getPassword());
        verify(userRepository).save(admin);
    }

    @Test
    public void testDoesNotTouchPasswordsAlreadyHashed() {
        User ana = user("ana@finanzas.com", "$2a$10$yaCifrada");
        when(userRepository.findAll()).thenReturn(List.of(ana));

        databaseInitializer.run(null);

        assertEquals("$2a$10$yaCifrada", ana.getPassword());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testHashesOnlyPendingUsers() {
        User admin = user("admin@finanzas.com", "PENDING_HASH");
        User ana = user("ana@finanzas.com", "PENDING_HASH");
        User juan = user("juan@finanzas.com", "$2a$10$yaCifrada");
        when(userRepository.findAll()).thenReturn(List.of(admin, ana, juan));
        when(passwordEncoder.encode("admin123")).thenReturn("HASH_ADMIN");
        when(passwordEncoder.encode("ana123")).thenReturn("HASH_ANA");

        databaseInitializer.run(null);

        assertEquals("HASH_ADMIN", admin.getPassword());
        assertEquals("HASH_ANA", ana.getPassword());
        verify(userRepository, times(2)).save(any(User.class));
    }
}
