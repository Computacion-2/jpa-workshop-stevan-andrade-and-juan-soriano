package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.model.Permission;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.RoleRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private RoleService roleService;

    private Permission permission1;
    private Permission permission2;
    private Role role;

    @BeforeEach
    public void setUp() {
        permission1 = new Permission();
        permission1.setId(1L);
        permission1.setName("USER_READ");

        permission2 = new Permission();
        permission2.setId(2L);
        permission2.setName("USER_WRITE");

        // El rol arranca con un permiso (un rol nunca debe quedar sin permisos)
        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.addPermission(permission1);
    }

    // Query

    @Test
    public void testFindAll() {
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getName());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Role result = roleService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ADMIN", result.getName());
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roleService.findById(99L));
    }

    // Insert

    @Test
    public void testCreate() {
        Set<Long> permissionIds = new HashSet<>();
        permissionIds.add(1L);
        when(permissionService.findById(1L)).thenReturn(permission1);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.create("ADMIN", permissionIds);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("ADMIN", result.getName());
        verify(permissionService, times(1)).findById(1L);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    public void testCreateWithNullPermissions() {
        assertThrows(IllegalArgumentException.class, () -> roleService.create("ADMIN", null));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testCreateWithEmptyPermissions() {
        Set<Long> permissionIds = new HashSet<>();

        assertThrows(IllegalArgumentException.class, () -> roleService.create("ADMIN", permissionIds));
        verify(roleRepository, never()).save(any(Role.class));
    }

    // Update

    @Test
    public void testUpdate() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.update(1L, "SUPERADMIN");

        assertEquals("SUPERADMIN", result.getName());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    public void testAddPermission() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionService.findById(2L)).thenReturn(permission2);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.addPermission(1L, 2L);

        assertEquals(2, result.getRolePermissions().size());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    public void testAddPermissionAlreadyAssigned() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionService.findById(1L)).thenReturn(permission1);

        assertThrows(IllegalArgumentException.class, () -> roleService.addPermission(1L, 1L));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testRemovePermission() {
        role.addPermission(permission2);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.removePermission(1L, 1L);

        assertEquals(1, result.getRolePermissions().size());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    public void testRemovePermissionNotAssigned() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        assertThrows(IllegalArgumentException.class, () -> roleService.removePermission(1L, 99L));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testRemoveLastPermission() {
        // El rol solo tiene un permiso, no se puede quitar
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        assertThrows(IllegalArgumentException.class, () -> roleService.removePermission(1L, 1L));
        verify(roleRepository, never()).save(any(Role.class));
    }

    // Delete

    @Test
    public void testDelete() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        roleService.delete(1L);

        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    public void testDeleteWithUsers() {
        // El rol tiene un usuario, no se debe poder borrar
        role.getUsers().add(new User());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        assertThrows(IllegalArgumentException.class, () -> roleService.delete(1L));
        verify(roleRepository, never()).delete(any(Role.class));
    }
}
