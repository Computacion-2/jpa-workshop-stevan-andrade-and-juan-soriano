package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionRequestDTO {

    @NotBlank(message = "El nombre del permiso es obligatorio")
    @Size(max = 80, message = "El nombre del permiso no puede tener mas de 80 caracteres")
    private String name;

    @Size(max = 255, message = "La descripcion no puede tener mas de 255 caracteres")
    private String description;

}
