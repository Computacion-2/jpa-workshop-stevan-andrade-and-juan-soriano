package com.example.service.interf;

import com.example.dto.LoginRequestDTO;
import com.example.dto.TokenResponseDTO;
import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;

public interface IAuthService {

    UserResponseDTO register(UserRequestDTO userRequestDTO, String confirmPassword);

    TokenResponseDTO login(LoginRequestDTO loginRequestDTO);

}
