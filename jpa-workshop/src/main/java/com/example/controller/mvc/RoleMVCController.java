package com.example.controller.mvc;

import java.util.HashSet;
import java.util.List;

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

import com.example.model.Role;
import com.example.service.PermissionService;
import com.example.service.RoleService;
import com.example.service.UserService;

@Controller
@RequestMapping("/app/roles")
public class RoleMVCController {

    private static final String ADMIN_ROLE = "ADMIN";

    private final RoleService roleService;
    private final PermissionService permissionService;

    public RoleMVCController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public String getAll(Model model) {
        model.addAttribute("roleList", roleService.findAll());
        return "roles/roles-list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public String createForm(Model model) {
        model.addAttribute("permissionList", permissionService.findAll());
        return "roles/role-form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public String create(@RequestParam String name,
                         @RequestParam(required = false) List<Long> permissionIds,
                         RedirectAttributes redirectAttributes) {
        if (name == null || name.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El nombre del rol es obligatorio.");
            return "redirect:/app/roles/new";
        }
        String normalizedName = name.trim().toUpperCase().replace(' ', '_');
        try {
            roleService.create(normalizedName, permissionIds == null ? null : new HashSet<>(permissionIds));
            redirectAttributes.addFlashAttribute("success", "Rol " + normalizedName + " creado.");
            return "redirect:/app/roles";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Ya existe un rol llamado " + normalizedName + ".");
        }
        redirectAttributes.addFlashAttribute("name", name);
        return "redirect:/app/roles/new";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('ROLE_WRITE')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Role role = roleService.findById(id);
            if (ADMIN_ROLE.equals(role.getName()) || UserService.DEFAULT_ROLE.equals(role.getName())) {
                redirectAttributes.addFlashAttribute("error",
                        "El rol " + role.getName() + " es del sistema y no se puede eliminar.");
                return "redirect:/app/roles";
            }
            roleService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Rol " + role.getName() + " eliminado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/roles";
    }
}
