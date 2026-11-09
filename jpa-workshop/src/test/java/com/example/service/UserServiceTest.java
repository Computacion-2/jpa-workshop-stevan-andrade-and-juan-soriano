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

import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.mapper.IUserMapper;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.interf.IRoleService;
import com.example.service.impl.UserService;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IRoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private IUserMapper userMapper = Mappers.getMapper(IUserMapper.class);

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

        List<UserResponseDTO> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.findById(1L);

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
        when(roleService.findByIdDB(1L)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserRequestDTO userRequestDTO = new UserRequestDTO("testuser", "testuser@example.com", "clave123", 1L);
        UserResponseDTO result = userService.create(userRequestDTO);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("testuser", result.getName());
        verify(roleService, times(1)).findByIdDB(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateWithoutRole() {
        // Un usuario no puede quedar sin rol
        UserRequestDTO userRequestDTO = new UserRequestDTO("testuser", "testuser@example.com", "clave123", null);
        assertThrows(IllegalArgumentException.class,
                () -> userService.create(userRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    // Update

    @Test
    public void testUpdate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleService.findByIdDB(1L)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRequestDTO userRequestDTO = new UserRequestDTO("editado", "editado@example.com", "otraclave", 1L);
        UserResponseDTO result = userService.update(1L, userRequestDTO);

        assertEquals("editado", result.getName());
        assertEquals("editado@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateWithoutRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserRequestDTO userRequestDTO = new UserRequestDTO("editado", "editado@example.com", "otraclave", null);
        assertThrows(IllegalArgumentException.class,
                () -> userService.update(1L, userRequestDTO));
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

    @Test
    public void testCreateHashesPassword() {
        when(roleService.findByIdDB(1L)).thenReturn(role);
        when(passwordEncoder.encode("clave123")).thenReturn("$2a$10$hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.create(new UserRequestDTO("testuser", "testuser@example.com", "clave123", 1L));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals("$2a$10$hash", saved.getValue().getPassword());
        verify(passwordEncoder, times(1)).encode("clave123");
    }

    @Test
    public void testCreateWithoutPassword() {
        UserRequestDTO userRequestDTO = new UserRequestDTO("testuser", "testuser@example.com", " ", 1L);

        assertThrows(IllegalArgumentException.class, () -> userService.create(userRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testCreateDuplicateEmail() {
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(true);

        UserRequestDTO userRequestDTO = new UserRequestDTO("testuser", "testuser@example.com", "clave123", 1L);

        assertThrows(IllegalArgumentException.class, () -> userService.create(userRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateKeepsIdAndHashesPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleService.findByIdDB(1L)).thenReturn(role);
        when(passwordEncoder.encode("otraclave")).thenReturn("$2a$10$otrohash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.update(1L, new UserRequestDTO("editado", "editado@example.com", "otraclave", 1L));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals(1L, saved.getValue().getId());
        assertEquals("$2a$10$otrohash", saved.getValue().getPassword());
    }

    @Test
    public void testUpdateWithoutPasswordKeepsCurrent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleService.findByIdDB(1L)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.update(1L, new UserRequestDTO("editado", "testuser@example.com", null, 1L));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals("clave123", saved.getValue().getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    public void testUpdateDuplicateEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleService.findByIdDB(1L)).thenReturn(role);
        when(userRepository.existsByEmailAndIdNot("ocupado@example.com", 1L)).thenReturn(true);

        UserRequestDTO userRequestDTO = new UserRequestDTO("editado", "ocupado@example.com", null, 1L);

        assertThrows(IllegalArgumentException.class, () -> userService.update(1L, userRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testChangeRole() {
        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setName("USER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleService.findByIdDB(2L)).thenReturn(userRole);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.changeRole(1L, 2L);

        assertEquals(2L, user.getRole().getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testChangeRoleUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.changeRole(99L, 2L));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRemoveRole() {
        Role defaultRole = new Role();
        defaultRole.setId(2L);
        defaultRole.setName(UserService.DEFAULT_ROLE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleService.findByNameDB(UserService.DEFAULT_ROLE)).thenReturn(defaultRole);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.removeRole(1L);

        assertEquals(UserService.DEFAULT_ROLE, user.getRole().getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testRemoveRoleAlreadyDefault() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleService.findByNameDB(UserService.DEFAULT_ROLE)).thenReturn(role);

        assertThrows(IllegalArgumentException.class, () -> userService.removeRole(1L));
        verify(userRepository, never()).save(any(User.class));
    }
}
