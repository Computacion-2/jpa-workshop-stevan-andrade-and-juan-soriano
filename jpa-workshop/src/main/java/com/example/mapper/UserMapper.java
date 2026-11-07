package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.model.User;

@Mapper(componentModel = "spring")
public interface  UserMapper {
    
    @Mapping(target = "id", ignore = true)
    //Could be just role. I used entity DB name row attribute
    @Mapping(target = "role_id", ignore = true)
    @Mapping(target = "password", ignore = true)
    //Same situation as role_id
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    User toEntity(UserRequestDTO userRequestDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    UserResponseDTO toResponse(User user);

}
