package com.example.controller.mvc;

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

import com.example.dto.PermissionRequestDTO;
import com.example.dto.PermissionResponseDTO;
import com.example.model.Permission;
import com.example.service.interf.IPermissionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/app/permissions")
@RequiredArgsConstructor 
public class PermissionMVCController {

    private final IPermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public String getAll(Model model) {
        model.addAttribute("permissionList", permissionService.findAll());
        return "permissions/permissions-list";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String description,
                         RedirectAttributes redirectAttributes) {
        if (name == null || name.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El nombre del permiso es obligatorio.");
            return "redirect:/app/permissions";
        }
        String normalizedName = name.trim().toUpperCase().replace(' ', '_');
        PermissionRequestDTO permissionRequestDTO = new PermissionRequestDTO(normalizedName, description.trim());
        try {
            permissionService.create(permissionRequestDTO);
            redirectAttributes.addFlashAttribute("success", "Permiso " + normalizedName + " creado.");
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Ya existe un permiso llamado " + normalizedName + ".");
        }
        return "redirect:/app/permissions";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            PermissionResponseDTO permission = permissionService.findById(id);
            permissionService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Permiso " + permission.getName() + " eliminado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/permissions";
    }
}
