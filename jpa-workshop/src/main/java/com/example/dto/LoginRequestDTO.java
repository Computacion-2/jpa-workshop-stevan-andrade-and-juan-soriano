package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @Schema(description = "Correo del usuario", example = "admin@finanzas.com")
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    @Schema(description = "Contraseña", example = "admin123")
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

}
