package com.example.controller;

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

import com.example.exception.PermissionEmptyException;
import com.example.exception.PermissionNoFoundException;
import com.example.model.Permission;
import com.example.service.interf.IPermissionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/app/permissions")
@RequiredArgsConstructor
public class PermissionMVCController {

    private final IPermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSIONS_READ')")
    public String getAll(Model model) {
        model.addAttribute("permissionList", permissionService.findAll());
        return "permissions/permissions-list";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSIONS_WRITE')")
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String description,
                         RedirectAttributes redirectAttributes) {
        if (name == null || name.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El nombre del permiso es obligatorio.");
            return "redirect:/app/permissions";
        }

        String normalizedName = name.trim().toUpperCase().replace(' ', '_');

        try {
            permissionService.create(Permission.builder()
                    .name(normalizedName)
                    .description(description == null ? null : description.trim())
                    .build());
            redirectAttributes.addFlashAttribute("success", "Permiso " + normalizedName + " creado.");
        } catch (PermissionEmptyException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/permissions";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('PERMISSIONS_WRITE')")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            Permission permission = permissionService.findById(id);
            permissionService.delete(permission);
            redirectAttributes.addFlashAttribute("success", "Permiso " + permission.getName() + " eliminado.");
        } catch (PermissionEmptyException | PermissionNoFoundException e) {
            redirectAttributes.addFlashAttribute("error", "El permiso no existe.");
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar: el permiso está asociado a un rol. Quítalo del rol primero.");
        }
        return "redirect:/app/permissions";
    }
}
