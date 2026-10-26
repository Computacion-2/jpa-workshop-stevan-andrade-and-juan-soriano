package com.example.service.impl;

import java.sql.Timestamp;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.exception.RegistrationException;
import com.example.exception.UserEmptyException;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.IRoleRepository;
import com.example.repository.IUserRepository;
import com.example.service.interf.IAuthService;
import com.example.service.interf.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    public static final String DEFAULT_ROLE = "USER";
    private static final int MIN_PASSWORD_LENGTH = 6;

    private final IUserService userService;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(String name, String email, String password, String confirmPassword)
            throws RegistrationException {

        if (isBlank(name) || isBlank(email) || isBlank(password)) {
            throw new RegistrationException("Completa el usuario, el correo y la contraseña.");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new RegistrationException("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
        }
        if (!password.equals(confirmPassword)) {
            throw new RegistrationException("Las contraseñas no coinciden.");
        }

        String username = name.trim();
        if (userRepository.findByName(username).isPresent()) {
            throw new RegistrationException("El usuario '" + username + "' ya existe.");
        }

        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE).orElse(null);

        User user = User.builder()
                .name(username)
                .email(email.trim())
                // la contraseña se guarda cifrada, nunca en texto plano
                .password(passwordEncoder.encode(password))
                .role(defaultRole)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        try {
            userService.create(user);
        } catch (UserEmptyException e) {
            throw new RegistrationException(e.getMessage());
        }
        return user;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
