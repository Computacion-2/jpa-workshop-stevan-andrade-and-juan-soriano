package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.model.Permission;
import com.example.model.RolePermission;
import com.example.repository.PermissionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    private Permission permission;

    @BeforeEach
    public void setUp() {
        permission = new Permission();
        permission.setId(1L);
        permission.setName("USER_READ");
        permission.setDescription("Consultar usuarios");
    }

    // Query

    @Test
    public void testFindAll() {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(permission);
        when(permissionRepository.findAll()).thenReturn(permissions);

        List<Permission> result = permissionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("USER_READ", result.get(0).getName());
        verify(permissionRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        Permission result = permissionService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("USER_READ", result.getName());
        verify(permissionRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(permissionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> permissionService.findById(99L));
    }

    // Insert

    @Test
    public void testCreate() {
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        Permission result = permissionService.create("USER_READ", "Consultar usuarios");

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("USER_READ", result.getName());
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    // Update

    @Test
    public void testUpdate() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        Permission result = permissionService.update(1L, "USER_WRITE", "Editar usuarios");

        assertEquals("USER_WRITE", result.getName());
        assertEquals("Editar usuarios", result.getDescription());
        verify(permissionRepository, times(1)).save(permission);
    }

    // Delete

    @Test
    public void testDelete() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        permissionService.delete(1L);

        verify(permissionRepository, times(1)).delete(permission);
    }

    @Test
    public void testDeleteWithRoles() {
        permission.getRolePermissions().add(new RolePermission());
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        assertThrows(IllegalArgumentException.class, () -> permissionService.delete(1L));
        verify(permissionRepository, never()).delete(any(Permission.class));
    }
}
