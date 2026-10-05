package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    private Role role;
    private User user;

    @BeforeEach
    public void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        user = new User();
        user.setId(1L);
        user.setName("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("clave123");
        user.setRole(role);
    }

    // Query

    @Test
    public void testFindAll() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findById(99L));
    }

    // Insert

    @Test
    public void testCreate() {
        when(roleService.findById(1L)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create("testuser", "testuser@example.com", "clave123", 1L);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("testuser", result.getName());
        assertEquals(role, result.getRole());
        verify(roleService, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateWithoutRole() {
        // Un usuario no puede quedar sin rol
        assertThrows(IllegalArgumentException.class,
                () -> userService.create("testuser", "testuser@example.com", "clave123", null));
        verify(userRepository, never()).save(any(User.class));
    }

    // Update

    @Test
    public void testUpdate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleService.findById(1L)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.update(1L, "editado", "editado@example.com", "otraclave", 1L);

        assertEquals("editado", result.getName());
        assertEquals("editado@example.com", result.getEmail());
        assertEquals("otraclave", result.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateWithoutRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.update(1L, "editado", "editado@example.com", "otraclave", null));
        verify(userRepository, never()).save(any(User.class));
    }

    // Delete

    @Test
    public void testDelete() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.delete(99L));
        verify(userRepository, never()).delete(any(User.class));
    }
}
