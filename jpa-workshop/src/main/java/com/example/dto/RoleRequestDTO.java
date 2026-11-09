package com.example.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDTO {

    @Schema(description = "Nombre del rol", example = "AUDITOR")
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 50, message = "El nombre del rol no puede tener mas de 50 caracteres")
    String name;

    @Schema(description = "Ids de los permisos del rol", example = "[1, 3]")
    Set<Long> permissionIds;

}
