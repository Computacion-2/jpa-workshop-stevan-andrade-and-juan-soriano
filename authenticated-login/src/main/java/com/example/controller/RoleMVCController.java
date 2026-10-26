package com.example.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.config.DataInitializer;
import com.example.exception.PermissionEmptyException;
import com.example.exception.PermissionNoFoundException;
import com.example.exception.RoleEmptyExcepcion;
import com.example.exception.RoleNoFoundException;
import com.example.model.Role;
import com.example.service.interf.IPermissionService;
import com.example.service.interf.IRoleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/app/roles")
@RequiredArgsConstructor
public class RoleMVCController {

    private final IRoleService roleService;
    private final IPermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLES_READ')")
    public String getAll(Model model) {
        model.addAttribute("roleList", roleService.findAll());
        return "roles/roles-list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ROLES_WRITE')")
    public String createForm() {
        return "roles/role-form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('ROLES_WRITE')")
    public String create(@RequestParam String name, RedirectAttributes redirectAttributes) {
        if (name == null || name.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El nombre del rol es obligatorio.");
            return "redirect:/app/roles/new";
        }

        String normalizedName = name.trim().toUpperCase().replace(' ', '_');

        if (roleService.existsByName(normalizedName)) {
            redirectAttributes.addFlashAttribute("error", "Ya existe un rol llamado " + normalizedName + ".");
            redirectAttributes.addFlashAttribute("name", name);
            return "redirect:/app/roles/new";
        }

        try {
            roleService.create(Role.builder().name(normalizedName).build());
            redirectAttributes.addFlashAttribute("success",
                    "Rol " + normalizedName + " creado. Ahora puedes asociarle permisos.");
        } catch (RoleEmptyExcepcion e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/app/roles/new";
        }
        return "redirect:/app/roles";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('ROLES_WRITE')")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            Role role = roleService.findById(id);

            if (DataInitializer.ADMIN_ROLE.equals(role.getName())
                    || DataInitializer.USER_ROLE.equals(role.getName())) {
                redirectAttributes.addFlashAttribute("error",
                        "El rol " + role.getName() + " es del sistema y no se puede eliminar.");
                return "redirect:/app/roles";
            }

            roleService.delete(role);
            redirectAttributes.addFlashAttribute("success", "Rol " + role.getName() + " eliminado.");
        } catch (RoleEmptyExcepcion | RoleNoFoundException e) {
            redirectAttributes.addFlashAttribute("error", "El rol no existe.");
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar: hay usuarios con este rol. Quítaselo primero desde Usuarios.");
        }
        return "redirect:/app/roles";
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLES_WRITE')")
    public String permissionsForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Role role = roleService.findById(id);
            Set<Integer> selectedIds = role.getRolePermissions()
                    .stream()
                    .map(rolePermission -> rolePermission.getPermission().getId())
                    .collect(Collectors.toSet());

            model.addAttribute("role", role);
            model.addAttribute("permissionList", permissionService.findAll());
            model.addAttribute("selectedIds", selectedIds);
            return "roles/role-permissions";
        } catch (RoleEmptyExcepcion | RoleNoFoundException e) {
            redirectAttributes.addFlashAttribute("error", "El rol no existe.");
            return "redirect:/app/roles";
        }
    }

    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLES_WRITE')")
    public String assignPermissions(@PathVariable int id,
                                    @RequestParam(required = false) List<Integer> permissionIds,
                                    RedirectAttributes redirectAttributes) {
        try {
            roleService.assignPermissions(id, permissionIds);
            redirectAttributes.addFlashAttribute("success",
                    "Permisos actualizados. Los usuarios con este rol los verán al volver a iniciar sesión.");
        } catch (RoleEmptyExcepcion | RoleNoFoundException e) {
            redirectAttributes.addFlashAttribute("error", "El rol no existe.");
            return "redirect:/app/roles";
        } catch (PermissionEmptyException | PermissionNoFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Uno de los permisos seleccionados no existe.");
            return "redirect:/app/roles/" + id + "/permissions";
        }
        return "redirect:/app/roles";
    }
}
