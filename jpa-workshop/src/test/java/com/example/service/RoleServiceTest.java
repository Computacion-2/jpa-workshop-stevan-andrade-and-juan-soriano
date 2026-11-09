package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.exception.ResourceNotFoundException;
import com.example.dto.RoleRequestDTO;
import com.example.dto.RoleResponseDTO;
import com.example.mapper.IRoleMapper;
import com.example.model.Permission;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.RoleRepository;
import com.example.service.interf.IPermissionService;
import com.example.service.impl.RoleService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private IPermissionService permissionService;

    @Spy
    private IRoleMapper roleMapper = Mappers.getMapper(IRoleMapper.class);

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

        List<RoleResponseDTO> result = roleService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getName());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        RoleResponseDTO result = roleService.findById(1L);

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
        when(permissionService.findByIdDB(1L)).thenReturn(permission1);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleRequestDTO roleRequestDTO = new RoleRequestDTO("ADMIN", permissionIds);

        RoleResponseDTO result = roleService.create(roleRequestDTO);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("ADMIN", result.getName());
        verify(permissionService, times(1)).findByIdDB(1L);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    public void testCreateWithNullPermissions() {
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO("ADMIN", null);
        assertThrows(IllegalArgumentException.class, () -> roleService.create(roleRequestDTO));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testCreateWithEmptyPermissions() {
        Set<Long> permissionIds = new HashSet<>();

        RoleRequestDTO roleRequestDTO = new RoleRequestDTO("ADMIN", permissionIds);
        assertThrows(IllegalArgumentException.class, () -> roleService.create(roleRequestDTO));
        verify(roleRepository, never()).save(any(Role.class));
    }

    // Update

    @Test
    public void testUpdate() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoleRequestDTO roleRequestDTO = new RoleRequestDTO("SUPERADMIN", null);
        RoleResponseDTO result = roleService.update(1L, roleRequestDTO);

        assertEquals("SUPERADMIN", result.getName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    public void testAddPermission() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionService.findByIdDB(2L)).thenReturn(permission2);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        roleService.addPermission(1L, 2L);
        Role roleFound = roleService.findByIdDB(1L);

        assertEquals(2, roleFound.getRolePermissions().size());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    public void testAddPermissionAlreadyAssigned() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionService.findByIdDB(1L)).thenReturn(permission1);

        assertThrows(IllegalArgumentException.class, () -> roleService.addPermission(1L, 1L));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testRemovePermission() {
        role.addPermission(permission2);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        roleService.removePermission(1L, 1L);
        Role roleFound = roleService.findByIdDB(1L);

        assertEquals(1, roleFound.getRolePermissions().size());
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

    @Test
    public void testFindByName() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

        RoleResponseDTO result = roleService.findByName("ADMIN");

        assertEquals(1L, result.getId());
        assertEquals("ADMIN", result.getName());
        verify(roleRepository, times(1)).findByName("ADMIN");
    }

    @Test
    public void testFindByNameNotFound() {
        when(roleRepository.findByName("NO_EXISTE")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roleService.findByName("NO_EXISTE"));
    }

    @Test
    public void testFindByNameBlank() {
        assertThrows(IllegalArgumentException.class, () -> roleService.findByName(" "));
        assertThrows(IllegalArgumentException.class, () -> roleService.findByName(null));
        verify(roleRepository, never()).findByName(any());
    }

    @Test
    public void testSetPermissionsReplacesPermissions() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionService.findByIdDB(2L)).thenReturn(permission2);
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        roleService.setPermissions(1L, Set.of(2L));

        assertEquals(1, role.getRolePermissions().size());
        assertEquals(2L, role.getRolePermissions().iterator().next().getPermission().getId());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    public void testSetPermissionsKeepsExisting() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionService.findByIdDB(2L)).thenReturn(permission2);
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        roleService.setPermissions(1L, Set.of(1L, 2L));

        assertEquals(2, role.getRolePermissions().size());
        verify(permissionService, never()).findByIdDB(1L);
        verify(permissionService, times(1)).findByIdDB(2L);
    }

    @Test
    public void testSetPermissionsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> roleService.setPermissions(1L, new HashSet<>()));
        assertThrows(IllegalArgumentException.class, () -> roleService.setPermissions(1L, null));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testFindByIdNotFoundIsResourceNotFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> roleService.findById(99L));
        assertEquals("Rol no encontrado con id 99", ex.getMessage());
    }

    @Test
    public void testFindByNameNotFoundIsResourceNotFound() {
        when(roleRepository.findByName("NO_EXISTE")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> roleService.findByName("NO_EXISTE"));
        assertEquals("Rol no encontrado: NO_EXISTE", ex.getMessage());
    }
}
