package com.example.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.controller.PermissionController.PermissionResponse;
import com.example.model.Role;
import com.example.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    /** En POST se usan name y permissionIds. En PUT solo name (los permisos se cambian con los endpoints de abajo). */
    public record RoleRequest(String name, Set<Long> permissionIds) {
    }

    public record RoleResponse(Long id, String name, List<PermissionResponse> permissions) {
        public static RoleResponse from(Role role) {
            List<PermissionResponse> permissions = role.getRolePermissions().stream()
                    .map(rp -> PermissionResponse.from(rp.getPermission()))
                    .toList();
            return new RoleResponse(role.getId(), role.getName(), permissions);
        }
    }

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleResponse> findAll() {
        return roleService.findAll().stream().map(RoleResponse::from).toList();
    }

    @GetMapping("/{id}")
    public RoleResponse findById(@PathVariable Long id) {
        return RoleResponse.from(roleService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse create(@RequestBody RoleRequest request) {
        return RoleResponse.from(roleService.create(request.name(), request.permissionIds()));
    }

    @PutMapping("/{id}")
    public RoleResponse update(@PathVariable Long id, @RequestBody RoleRequest request) {
        return RoleResponse.from(roleService.update(id, request.name()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public RoleResponse addPermission(@PathVariable Long roleId, @PathVariable Long permissionId) {
        return RoleResponse.from(roleService.addPermission(roleId, permissionId));
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public RoleResponse removePermission(@PathVariable Long roleId, @PathVariable Long permissionId) {
        return RoleResponse.from(roleService.removePermission(roleId, permissionId));
    }
}
