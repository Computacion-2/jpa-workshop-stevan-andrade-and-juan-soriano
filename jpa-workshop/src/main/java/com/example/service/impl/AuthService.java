package com.example.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dto.LoginRequestDTO;
import com.example.dto.RoleResponseDTO;
import com.example.dto.TokenResponseDTO;
import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.repository.UserRepository;
import com.example.security.AppUserDetails;
import com.example.service.interf.IAuthService;
import com.example.service.interf.IJwtService;
import com.example.service.interf.IRoleService;
import com.example.service.interf.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class AuthService implements IAuthService {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String INVALID_CREDENTIALS = "Correo o contraseña incorrectos";
    private static final String TOKEN_TYPE = "Bearer";

    private final IUserService userService;
    private final IRoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final IJwtService jwtService;

    @Override
    public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
        if (loginRequestDTO == null || isBlank(loginRequestDTO.getEmail()) || isBlank(loginRequestDTO.getPassword())) {
            throw new IllegalArgumentException("El correo y la contraseña son obligatorios.");
        }

        AppUserDetails userDetails;
        try {
            userDetails = (AppUserDetails) userDetailsService.loadUserByUsername(loginRequestDTO.getEmail().trim());
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }

        String token = jwtService.generateToken(userDetails);
        return TokenResponseDTO.builder()
                .accessToken(token)
                .tokenType(TOKEN_TYPE)
                .expiresIn(jwtService.getExpirationTime() / 1000)
                .build();
    }

    @Override 
    public UserResponseDTO register(UserRequestDTO userRequestDTO, String confirmPassword) {
        if (isBlank(userRequestDTO.getName()) || isBlank(userRequestDTO.getEmail()) || isBlank(userRequestDTO.getPassword())) {
            throw new IllegalArgumentException("Completa el nombre, el correo y la contraseña.");
        }
        if (userRequestDTO.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
        }
        if (!userRequestDTO.getPassword().equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }
        String cleanEmail = userRequestDTO.getEmail().trim();
        if (userRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("Ya existe una cuenta con el correo " + cleanEmail + ".");
        }

        RoleResponseDTO defaultRole = roleService.findByName(UserService.DEFAULT_ROLE);
        userRequestDTO.setRoleId(defaultRole.getId());
        // la contraseña se guarda cifrada, nunca en texto plano
        userRequestDTO.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        return userService.create(userRequestDTO);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
