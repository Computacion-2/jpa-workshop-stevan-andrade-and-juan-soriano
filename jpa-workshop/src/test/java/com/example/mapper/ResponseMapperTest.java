package com.example.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.example.dto.PermissionResponseDTO;
import com.example.dto.RoleResponseDTO;
import com.example.dto.UserResponseDTO;
import com.example.model.Permission;
import com.example.model.Role;
import com.example.model.User;

public class ResponseMapperTest {

    private final IUserMapper userMapper = Mappers.getMapper(IUserMapper.class);
    private final IRoleMapper roleMapper = Mappers.getMapper(IRoleMapper.class);

    private Role role;
    private User user;

    @BeforeEach
    public void setUp() {
        Permission read = new Permission();
        read.setId(1L);
        read.setName("USER_READ");
        read.setDescription("Consultar usuarios");

        Permission write = new Permission();
        write.setId(2L);
        write.setName("USER_WRITE");

        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.addPermission(read);
        role.addPermission(write);

        user = new User();
        user.setId(1L);
        user.setName("Administrador");
        user.setEmail("admin@finanzas.com");
        user.setPassword("$2a$10$hash");
        user.setRole(role);
        role.getUsers().add(user);
    }

    private Set<String> names(RoleResponseDTO dto) {
        return dto.getPermissions().stream().map(PermissionResponseDTO::getName).collect(Collectors.toSet());
    }

    @Test
    public void testRoleResponseHasPermissionsAndUserCount() {
        RoleResponseDTO dto = roleMapper.toResponse(role);

        assertEquals("ADMIN", dto.getName());
        assertEquals(Set.of("USER_READ", "USER_WRITE"), names(dto));
        assertEquals(1, dto.getUserCount());
    }

    @Test
    public void testUserResponseHasRoleWithPermissions() {
        UserResponseDTO dto = userMapper.toResponse(user);

        assertEquals("admin@finanzas.com", dto.getEmail());
        assertEquals(1L, dto.getRole().getId());
        assertEquals("ADMIN", dto.getRole().getName());
        assertEquals(Set.of("USER_READ", "USER_WRITE"), names(dto.getRole()));
    }

    @Test
    public void testRoleWithoutPermissionsHasEmptyList() {
        Role empty = new Role();
        empty.setId(3L);
        empty.setName("VACIO");

        RoleResponseDTO dto = roleMapper.toResponse(empty);

        assertTrue(dto.getPermissions().isEmpty());
        assertEquals(0, dto.getUserCount());
    }

    @Test
    public void testUserWithoutRole() {
        user.setRole(null);

        assertNull(userMapper.toResponse(user).getRole());
    }
}
