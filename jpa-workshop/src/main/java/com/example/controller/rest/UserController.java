package com.example.controller.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.config.OpenApiConfig;
import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.service.interf.IUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "CRUD de usuarios. Lectura con USER_READ, escritura con USER_WRITE")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Sin token, token alterado o vencido", content = @Content),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene el permiso necesario", content = @Content)
})
public class UserController {

    private final IUserService userService;

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Requiere USER_READ.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios con su rol y permisos")
    public List<UserResponseDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por id", description = "Requiere USER_READ.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id", content = @Content)
    })
    public UserResponseDTO findById(@Parameter(description = "Id del usuario", example = "1") @PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear usuario",
            description = "Requiere USER_WRITE. La contraseña es obligatoria y se guarda cifrada con BCrypt.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o correo ya registrado", content = @Content),
            @ApiResponse(responseCode = "404", description = "El rol indicado no existe", content = @Content)
    })
    public UserResponseDTO create(@Valid @RequestBody UserRequestDTO request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario",
            description = "Requiere USER_WRITE. Si no se envia contraseña, se conserva la actual.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o correo ya registrado", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe el usuario o el rol", content = @Content)
    })
    public UserResponseDTO update(@Parameter(description = "Id del usuario", example = "1") @PathVariable Long id,
                                  @Valid @RequestBody UserRequestDTO request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar usuario", description = "Requiere USER_WRITE.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id", content = @Content),
            @ApiResponse(responseCode = "409", description = "El usuario tiene datos asociados", content = @Content)
    })
    public void delete(@Parameter(description = "Id del usuario", example = "1") @PathVariable Long id) {
        userService.delete(id);
    }
}
