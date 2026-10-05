package com.example.service;

import com.example.model.Permission;
import com.example.model.Role;
import com.example.model.RolePermission;
import com.example.repository.RoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public RoleService(RoleRepository roleRepository, PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findById(Long id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isEmpty()) {
            throw new IllegalArgumentException("Rol no encontrado: " + id);
        }
        return role.get();
    }

    public Role create(String name, Set<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new IllegalArgumentException("El rol debe tener al menos un permiso");
        }
        Role role = new Role();
        role.setName(name);
        for (Long permissionId : permissionIds) {
            Permission permission = permissionService.findById(permissionId);
            role.addPermission(permission);
        }
        return roleRepository.save(role);
    }

    public Role update(Long id, String name) {
        Role role = findById(id);
        role.setName(name);
        return roleRepository.save(role);
    }

    public void delete(Long id) {
        Role role = findById(id);
        // Si hay usuarios con este rol, quedarian sin rol
        if (!role.getUsers().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar un rol que tiene usuarios");
        }
        roleRepository.delete(role);
    }

    public Role addPermission(Long roleId, Long permissionId) {
        Role role = findById(roleId);
        Permission permission = permissionService.findById(permissionId);
        for (RolePermission rolePermission : role.getRolePermissions()) {
            if (rolePermission.getPermission().getId().equals(permissionId)) {
                throw new IllegalArgumentException("El rol ya tiene ese permiso");
            }
        }
        role.addPermission(permission);
        return roleRepository.save(role);
    }

    public Role removePermission(Long roleId, Long permissionId) {
        Role role = findById(roleId);
        RolePermission toRemove = null;
        for (RolePermission rolePermission : role.getRolePermissions()) {
            if (rolePermission.getPermission().getId().equals(permissionId)) {
                toRemove = rolePermission;
            }
        }
        if (toRemove == null) {
            throw new IllegalArgumentException("El rol no tiene ese permiso");
        }
        if (role.getRolePermissions().size() == 1) {
            throw new IllegalArgumentException("El rol debe conservar al menos un permiso");
        }
        role.getRolePermissions().remove(toRemove);
        return roleRepository.save(role);
    }
}
