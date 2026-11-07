package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.RoleRequestDTO;
import com.example.dto.RoleResponseDTO;
import com.example.model.Role;

@Mapper(componentModel = "spring")
public interface  RoleMapper {
    
    @Mapping(target = "id", ignore = true)
    Role toEntity(RoleRequestDTO roleRequestDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoleResponseDTO toResponse(Role role);

}
