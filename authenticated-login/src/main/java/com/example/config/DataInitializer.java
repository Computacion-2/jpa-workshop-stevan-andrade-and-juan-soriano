package com.example.config;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.model.Permission;
import com.example.model.Role;
import com.example.model.RolePermission;
import com.example.model.User;
import com.example.repository.IPermissionRepository;
import com.example.repository.IRoleRepository;
import com.example.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private final IPermissionRepository permissionRepository;
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, String> permissions = new LinkedHashMap<>();
        permissions.put("USERS_READ", "Consultar la lista de usuarios");
        permissions.put("USERS_WRITE", "Asignar y quitar roles a los usuarios");
        permissions.put("ROLES_READ", "Consultar los roles y sus permisos");
        permissions.put("ROLES_WRITE", "Crear, eliminar roles y asociarles permisos");
        permissions.put("PERMISSIONS_READ", "Consultar los permisos");
        permissions.put("PERMISSIONS_WRITE", "Crear y eliminar permisos");

        permissions.forEach((name, description) -> {
            if (permissionRepository.findByName(name).isEmpty()) {
                permissionRepository.save(Permission.builder()
                        .name(name)
                        .description(description)
                        .build());
            }
        });

        Role adminRole = createRoleIfMissing(ADMIN_ROLE, List.copyOf(permissions.keySet()));
        createRoleIfMissing(USER_ROLE, List.of("USERS_READ"));

        if (userRepository.findByName(ADMIN_USERNAME).isEmpty()) {
            userRepository.save(User.builder()
                    .name(ADMIN_USERNAME)
                    .email("admin@localhost")
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .role(adminRole)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build());
        }
    }

    private Role createRoleIfMissing(String roleName, List<String> permissionNames) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = Role.builder().name(roleName).build();
            for (String permissionName : permissionNames) {
                permissionRepository.findByName(permissionName).ifPresent(permission ->
                        role.getRolePermissions().add(RolePermission.builder()
                                .role(role)
                                .permission(permission)
                                .build()));
            }
            return roleRepository.save(role);
        });
    }
}
