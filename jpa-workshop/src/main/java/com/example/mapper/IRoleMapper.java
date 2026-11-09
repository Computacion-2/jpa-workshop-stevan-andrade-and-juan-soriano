package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.PermissionResponseDTO;
import com.example.dto.RoleRequestDTO;
import com.example.dto.RoleResponseDTO;
import com.example.model.Role;
import com.example.model.RolePermission;

@Mapper(componentModel = "spring")
public interface IRoleMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rolePermissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleRequestDTO roleRequestDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "permissions", source = "rolePermissions")
    @Mapping(target = "userCount", expression = "java(role.getUsers() == null ? 0 : role.getUsers().size())")
    RoleResponseDTO toResponse(Role role);

    @Mapping(target = "id", source = "permission.id")
    @Mapping(target = "name", source = "permission.name")
    @Mapping(target = "description", source = "permission.description")
    PermissionResponseDTO toPermissionResponse(RolePermission rolePermission);

}
