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
import com.example.dto.PermissionRequestDTO;
import com.example.dto.PermissionResponseDTO;
import com.example.service.interf.IPermissionService;

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
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permisos", description = "CRUD de permisos. Lectura con PERMISSION_READ, escritura con PERMISSION_WRITE")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Sin token, token alterado o vencido", content = @Content),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene el permiso necesario", content = @Content)
})
public class PermissionController {

    private final IPermissionService permissionService;

    @GetMapping
    @Operation(summary = "Listar permisos", description = "Requiere PERMISSION_READ.")
    @ApiResponse(responseCode = "200", description = "Lista de permisos")
    public List<PermissionResponseDTO> findAll() {
        return permissionService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener permiso por id", description = "Requiere PERMISSION_READ.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permiso encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un permiso con ese id", content = @Content)
    })
    public PermissionResponseDTO findById(@Parameter(description = "Id del permiso", example = "1") @PathVariable Long id) {
        return permissionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear permiso", description = "Requiere PERMISSION_WRITE.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Permiso creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Ya existe un permiso con ese nombre", content = @Content)
    })
    public PermissionResponseDTO create(@Valid @RequestBody PermissionRequestDTO request) {
        return permissionService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar permiso", description = "Requiere PERMISSION_WRITE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permiso actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un permiso con ese id", content = @Content)
    })
    public PermissionResponseDTO update(@Parameter(description = "Id del permiso", example = "1") @PathVariable Long id,
                                        @Valid @RequestBody PermissionRequestDTO request) {
        return permissionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar permiso",
            description = "Requiere PERMISSION_WRITE. No se puede eliminar un permiso asignado a algun rol.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Permiso eliminado"),
            @ApiResponse(responseCode = "400", description = "El permiso esta asignado a un rol", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un permiso con ese id", content = @Content)
    })
    public void delete(@Parameter(description = "Id del permiso", example = "1") @PathVariable Long id) {
        permissionService.delete(id);
    }
}
