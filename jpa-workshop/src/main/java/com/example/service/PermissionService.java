package com.example.service;

import com.example.model.Permission;
import com.example.repository.PermissionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    public Permission findById(Long id) {
        Optional<Permission> permission = permissionRepository.findById(id);
        if (permission.isEmpty()) {
            throw new IllegalArgumentException("Permiso no encontrado: " + id);
        }
        return permission.get();
    }

    public Permission create(String name, String description) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        return permissionRepository.save(permission);
    }

    public Permission update(Long id, String name, String description) {
        Permission permission = findById(id);
        permission.setName(name);
        permission.setDescription(description);
        return permissionRepository.save(permission);
    }

    public void delete(Long id) {
        Permission permission = findById(id);
        if (!permission.getRolePermissions().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar un permiso que tiene roles asignados");
        }
        permissionRepository.delete(permission);
    }
}
