package com.example.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class RequestDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void tearDown() {
        factory.close();
    }

    private Set<String> invalidFields(Object dto) {
        return validator.validate(dto).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Test
    public void testValidUser() {
        UserRequestDTO dto = new UserRequestDTO("Ana Gómez", "ana@finanzas.com", "clave123", 2L);

        assertTrue(invalidFields(dto).isEmpty());
    }

    @Test
    public void testInvalidUser() {
        UserRequestDTO dto = new UserRequestDTO("", "no-es-correo", "123", null);

        assertEquals(Set.of("name", "email", "password", "roleId"), invalidFields(dto));
    }

    @Test
    public void testUserWithoutPasswordIsValidForUpdate() {
        UserRequestDTO dto = new UserRequestDTO("Ana Gómez", "ana@finanzas.com", null, 2L);

        assertTrue(invalidFields(dto).isEmpty());
    }

    @Test
    public void testInvalidRole() {
        RoleRequestDTO dto = new RoleRequestDTO(" ", Set.of(1L));

        assertEquals(Set.of("name"), invalidFields(dto));
    }

    @Test
    public void testInvalidPermission() {
        PermissionRequestDTO dto = new PermissionRequestDTO("", "Descripcion");

        assertEquals(Set.of("name"), invalidFields(dto));
    }

    @Test
    public void testInvalidLogin() {
        LoginRequestDTO dto = new LoginRequestDTO("", null);

        assertEquals(Set.of("email", "password"), invalidFields(dto));
    }

    @Test
    public void testInvalidRegister() {
        RegisterRequestDTO dto = new RegisterRequestDTO("", "correo", "123", "");

        assertEquals(Set.of("name", "email", "password", "confirmPassword"), invalidFields(dto));
    }
}
