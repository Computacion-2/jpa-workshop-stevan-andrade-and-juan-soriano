package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.PermissionRequestDTO;
import com.example.dto.PermissionResponseDTO;
import com.example.model.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    
    @Mapping(target = "id", ignore = true)
    Permission toEntity(PermissionRequestDTO permissionRequestDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    PermissionResponseDTO toResponse(Permission permission);
}
