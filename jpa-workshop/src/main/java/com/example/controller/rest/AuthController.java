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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "Endpoints publicos para iniciar sesion y registrarse")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion",
            description = "Verifica el correo y la contraseña y devuelve un token JWT firmado con HS256. "
                    + "El token lleva el correo (sub), el rol, los permisos, iat y exp.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciales correctas, devuelve el accessToken"),
            @ApiResponse(responseCode = "400", description = "Faltan el correo o la contraseña", content = @Content),
            @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos", content = @Content)
    })
    public TokenResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar usuario",
            description = "Crea una cuenta nueva con el rol USER. La contraseña se guarda cifrada con BCrypt.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400",
                    description = "Datos invalidos, contraseñas distintas o correo ya registrado", content = @Content)
    })
    public UserResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        return authService.register(userRequestDTO, request.getConfirmPassword());
    }
}
