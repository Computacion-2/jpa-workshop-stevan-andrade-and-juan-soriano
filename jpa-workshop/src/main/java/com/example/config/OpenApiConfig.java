package com.example.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Finanzas Personales - API REST",
        version = "1.0",
        description = "API REST del taller 4 de Computacion en Internet II. "
                + "Para usar los endpoints protegidos: ejecutar POST /api/auth/login, copiar el accessToken "
                + "y pegarlo en el boton Authorize.",
        contact = @Contact(name = "Stevan Andrade y Juan Soriano")))
@SecurityScheme(
        name = OpenApiConfig.BEARER_AUTH,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "Token JWT obtenido en POST /api/auth/login")
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";

}
