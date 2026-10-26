package com.example.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.model.User;
import com.example.repository.UserRepository;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final String PENDING_HASH = "PENDING_HASH";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // data.sql deja PENDING_HASH: la contraseña de prueba es lo que va antes de la @ seguido de 123
    @Override
    public void run(ApplicationArguments args) {
        for (User user : userRepository.findAll()) {
            if (PENDING_HASH.equals(user.getPassword())) {
                String email = user.getEmail();
                String rawPassword = email.substring(0, email.indexOf('@')) + "123";
                user.setPassword(passwordEncoder.encode(rawPassword));
                userRepository.save(user);
            }
        }
    }
}
