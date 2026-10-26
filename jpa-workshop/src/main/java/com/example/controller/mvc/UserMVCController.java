package com.example.controller.mvc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.model.User;
import com.example.service.RoleService;
import com.example.service.UserService;

@Controller
@RequestMapping("/app/users")
public class UserMVCController {

    private final UserService userService;
    private final RoleService roleService;

    public UserMVCController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public String getAll(Model model) {
        model.addAttribute("userList", userService.findAll());
        return "users/users-list";
    }

    @GetMapping("/{id}/role")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public String roleForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("roles", roleService.findAll());
            return "users/user-role";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/app/users";
        }
    }

    @PostMapping("/{id}/role")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public String changeRole(@PathVariable Long id,
                             @RequestParam Long roleId,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.changeRole(id, roleId);
            redirectAttributes.addFlashAttribute("success", "Rol asociado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/users/" + id + "/role";
    }

    @PostMapping("/{id}/role/remove")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public String removeRole(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            // no dejar que el usuario se quite su propio rol
            if (user.getEmail().equals(authentication.getName())) {
                redirectAttributes.addFlashAttribute("error", "No puedes quitarte el rol a ti mismo.");
                return "redirect:/app/users/" + id + "/role";
            }
            userService.removeRole(id);
            redirectAttributes.addFlashAttribute("success",
                    "Rol quitado a " + user.getName() + ". Ahora tiene el rol " + UserService.DEFAULT_ROLE + ".");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/users";
    }
}
