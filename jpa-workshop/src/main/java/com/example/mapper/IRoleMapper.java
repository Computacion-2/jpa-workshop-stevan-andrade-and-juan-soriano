package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.RoleRequestDTO;
import com.example.dto.RoleResponseDTO;
import com.example.model.Role;

@Mapper(componentModel = "spring")
public interface IRoleMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rolePermissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleRequestDTO roleRequestDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoleResponseDTO toResponse(Role role);

}
