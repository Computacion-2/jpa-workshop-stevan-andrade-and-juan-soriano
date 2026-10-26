
package com.example.security;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.exception.UserEmptyException;
import com.example.exception.UserNoFoundException;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.IRoleRepository;
import com.example.repository.IUserRepository;
import com.example.service.interf.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceApp implements UserDetailsService, ApplicationRunner {

    private static final String DEFAULT_USERNAME = "user";
    private static final String DEFAULT_PASSWORD = "passwordTest";
    private static final String DEFAULT_ROLE = "USER";

    private final IUserService userService;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        Role defaultRole = roleRepository.findAll().stream()
                .filter(role -> DEFAULT_ROLE.equals(role.getName()))
                .findFirst()
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(DEFAULT_ROLE)
                        .rolePermissions(new ArrayList<>())
                        .build()));

        Optional<User> existingUser = userRepository.findByName(DEFAULT_USERNAME);
        if (existingUser.isEmpty()) {
            userRepository.save(User.builder()
                    .name(DEFAULT_USERNAME)
                    .email("user@localhost")
                    .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                    .role(defaultRole)
                    .build());
            return;
        }

        User user = existingUser.get();
        boolean changed = false;
        if (user.getPassword() == null || !passwordEncoder.matches(DEFAULT_PASSWORD, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            changed = true;
        }
        if (user.getRole() == null) {
            user.setRole(defaultRole);
            changed = true;
        }
        if (changed) {
            userRepository.save(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String name)
            throws UsernameNotFoundException {

        try {
            User user = userService.findByName(name);

            if (user == null) {
                throw new UsernameNotFoundException(
                    "No existe el usuario: " + name
                );
            }

            return new UserDetailsApp(user);

        } catch (UserEmptyException | UserNoFoundException e) {
            throw new UsernameNotFoundException(
                "No se encontró el usuario: " + name, e
            );
        }
    }
}
