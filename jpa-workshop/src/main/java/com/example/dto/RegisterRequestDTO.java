package com.example.dto;

import jakarta.validation.constraints.Email;
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
public class RegisterRequestDTO {

    @Schema(description = "Nombre completo", example = "Carlos Ruiz")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener mas de 100 caracteres")
    private String name;

    @Schema(description = "Correo, debe ser unico", example = "carlos@finanzas.com")
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato valido")
    @Size(max = 120, message = "El correo no puede tener mas de 120 caracteres")
    private String email;

    @Schema(description = "Contraseña, minimo 6 caracteres", example = "clave123")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @Schema(description = "Repetir la contraseña", example = "clave123")
    @NotBlank(message = "La confirmacion de la contraseña es obligatoria")
    private String confirmPassword;

}
