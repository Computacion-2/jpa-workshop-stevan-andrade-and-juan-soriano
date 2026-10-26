package com.example.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;

@Service
public class AuthService {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private final UserService userService;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, RoleService roleService,
                       UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String name, String email, String password, String confirmPassword) {
        if (isBlank(name) || isBlank(email) || isBlank(password)) {
            throw new IllegalArgumentException("Completa el nombre, el correo y la contraseña.");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }
        String cleanEmail = email.trim();
        if (userRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("Ya existe una cuenta con el correo " + cleanEmail + ".");
        }

        Role defaultRole = roleService.findByName(UserService.DEFAULT_ROLE);
        // la contraseña se guarda cifrada, nunca en texto plano
        return userService.create(name.trim(), cleanEmail, passwordEncoder.encode(password), defaultRole.getId());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
