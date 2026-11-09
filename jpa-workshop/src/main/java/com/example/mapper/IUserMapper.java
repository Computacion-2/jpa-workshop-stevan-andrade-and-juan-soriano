package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.PermissionResponseDTO;
import com.example.dto.RoleResponseDTO;
import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.model.Role;
import com.example.model.RolePermission;
import com.example.model.User;

@Mapper(componentModel = "spring")
public interface  IUserMapper {
    
    @Mapping(target = "id", ignore = true)
    //Could be just role. I used entity DB name row attribute
    @Mapping(target = "role", ignore = true)
    //Same situation as role_id
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "periodSummaries", ignore = true)
    User toEntity(UserRequestDTO userRequestDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    UserResponseDTO toResponse(User user);

    @Mapping(target = "permissions", source = "rolePermissions")
    @Mapping(target = "userCount", expression = "java(role.getUsers() == null ? 0 : role.getUsers().size())")
    RoleResponseDTO toRoleResponse(Role role);

    @Mapping(target = "id", source = "permission.id")
    @Mapping(target = "name", source = "permission.name")
    @Mapping(target = "description", source = "permission.description")
    PermissionResponseDTO toPermissionResponse(RolePermission rolePermission);

}
