package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
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
    UserResponseDTO toResponse(User user);

}
