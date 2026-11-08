package com.example.service.interf;

import java.util.List;

import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;

public interface IUserService {
    
    List<UserResponseDTO> findAll();

    UserResponseDTO findById(Long id);

    UserResponseDTO create(UserRequestDTO userRequestDTO);

    UserResponseDTO update(Long id, UserRequestDTO userRequestDTO);

    UserResponseDTO changeRole(Long userId, Long roleId);

    UserResponseDTO removeRole(Long userId);

    UserResponseDTO delete(Long id);

}
