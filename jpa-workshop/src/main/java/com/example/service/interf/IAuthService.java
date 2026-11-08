package com.example.service.interf;

import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;

public interface IAuthService {

    UserResponseDTO register(UserRequestDTO userRequestDTO, String confirmPassword);

}
