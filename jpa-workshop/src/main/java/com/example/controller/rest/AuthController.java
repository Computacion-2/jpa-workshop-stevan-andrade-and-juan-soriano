package com.example.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.LoginRequestDTO;
import com.example.dto.RegisterRequestDTO;
import com.example.dto.TokenResponseDTO;
import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.service.interf.IAuthService;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public TokenResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        return authService.register(userRequestDTO, request.getConfirmPassword());
    }
}
