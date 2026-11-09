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
import com.example.dto.RoleRequestDTO;
import com.example.dto.RoleResponseDTO;
import com.example.service.interf.IRoleService;

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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "CRUD de roles y sus permisos. Lectura con ROLE_READ, escritura con ROLE_WRITE")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Sin token, token alterado o vencido", content = @Content),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene el permiso necesario", content = @Content)
})
public class RoleController {

    private final IRoleService roleService;

    @GetMapping
    @Operation(summary = "Listar roles", description = "Requiere ROLE_READ.")
    @ApiResponse(responseCode = "200", description = "Lista de roles con sus permisos y la cantidad de usuarios")
    public List<RoleResponseDTO> findAll() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por id", description = "Requiere ROLE_READ.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un rol con ese id", content = @Content)
    })
    public RoleResponseDTO findById(@Parameter(description = "Id del rol", example = "1") @PathVariable Long id) {
        return roleService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear rol", description = "Requiere ROLE_WRITE. El rol debe tener al menos un permiso.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rol creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o sin permisos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Alguno de los permisos no existe", content = @Content),
            @ApiResponse(responseCode = "409", description = "Ya existe un rol con ese nombre", content = @Content)
    })
    public RoleResponseDTO create(@Valid @RequestBody RoleRequestDTO request) {
        return roleService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar rol",
            description = "Requiere ROLE_WRITE. Cambia el nombre; si se envian permissionIds, reemplaza los permisos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe el rol o alguno de los permisos", content = @Content)
    })
    public RoleResponseDTO update(@Parameter(description = "Id del rol", example = "1") @PathVariable Long id,
                                  @Valid @RequestBody RoleRequestDTO request) {
        return roleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar rol", description = "Solo para el rol ADMIN. No se puede eliminar un rol con usuarios.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rol eliminado"),
            @ApiResponse(responseCode = "400", description = "El rol tiene usuarios asignados", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un rol con ese id", content = @Content)
    })
    public void delete(@Parameter(description = "Id del rol", example = "3") @PathVariable Long id) {
        roleService.delete(id);
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @Operation(summary = "Agregar un permiso al rol", description = "Requiere ROLE_WRITE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permiso agregado, devuelve el rol actualizado"),
            @ApiResponse(responseCode = "400", description = "El rol ya tiene ese permiso", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe el rol o el permiso", content = @Content)
    })
    public RoleResponseDTO addPermission(@Parameter(description = "Id del rol", example = "1") @PathVariable Long roleId,
                                         @Parameter(description = "Id del permiso", example = "1") @PathVariable Long permissionId) {
        return roleService.addPermission(roleId, permissionId);
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @Operation(summary = "Quitar un permiso del rol",
            description = "Requiere ROLE_WRITE. El rol debe conservar al menos un permiso.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permiso quitado, devuelve el rol actualizado"),
            @ApiResponse(responseCode = "400", description = "El rol no tiene ese permiso o es su ultimo permiso", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe el rol", content = @Content)
    })
    public RoleResponseDTO removePermission(@Parameter(description = "Id del rol", example = "1") @PathVariable Long roleId,
                                            @Parameter(description = "Id del permiso", example = "1") @PathVariable Long permissionId) {
        return roleService.removePermission(roleId, permissionId);
    }
}
