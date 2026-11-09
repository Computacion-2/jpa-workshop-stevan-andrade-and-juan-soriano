package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponseDTO {

    @Schema(description = "Token JWT para enviar en Authorization: Bearer <token>", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    private String tokenType;
    private long expiresIn;

}
