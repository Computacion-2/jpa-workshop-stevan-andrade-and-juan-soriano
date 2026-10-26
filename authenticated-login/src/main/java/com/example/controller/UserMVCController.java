package com.example.controller;

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

import com.example.exception.RoleEmptyExcepcion;
import com.example.exception.RoleNoFoundException;
import com.example.exception.UserEmptyException;
import com.example.exception.UserNoFoundException;
import com.example.model.User;
import com.example.service.interf.IRoleService;
import com.example.service.interf.IUserService;

import lombok.RequiredArgsConstructor;

@Controller 
@RequestMapping("/app/users")
@RequiredArgsConstructor 
public class UserMVCController {

    private final IUserService userService;
    private final IRoleService roleService;
    
    @GetMapping()
    @PreAuthorize("hasAuthority('USERS_READ')")
    public String getAll(Model model) {
        model.addAttribute("userList", userService.findAll());
        return "users/users-list";
    }

    @GetMapping("/{id}/role")
    @PreAuthorize("hasAuthority('USERS_WRITE')")
    public String roleForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("roles", roleService.findAll());
            return "users/user-role";
        } catch (UserEmptyException | UserNoFoundException e) {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe.");
            return "redirect:/app/users";
        }
    }

    @PostMapping("/{id}/role")
    @PreAuthorize("hasAuthority('USERS_WRITE')")
    public String assignRole(@PathVariable int id,
                             @RequestParam int roleId,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.assignRole(id, roleId);
            redirectAttributes.addFlashAttribute("success", "Rol asignado correctamente.");
        } catch (UserEmptyException | UserNoFoundException e) {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe.");
            return "redirect:/app/users";
        } catch (RoleEmptyExcepcion | RoleNoFoundException e) {
            redirectAttributes.addFlashAttribute("error", "El rol seleccionado no existe.");
        }
        return "redirect:/app/users/" + id + "/role";
    }

    @PostMapping("/{id}/role/remove")
    @PreAuthorize("hasAuthority('USERS_WRITE')")
    public String removeRole(@PathVariable int id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);

            // no dejar que el usuario se quite su propio rol
            if (user.getName().equals(authentication.getName())) {
                redirectAttributes.addFlashAttribute("error", "No puedes quitarte el rol a ti mismo.");
                return "redirect:/app/users/" + id + "/role";
            }

            userService.removeRole(id);
            redirectAttributes.addFlashAttribute("success", "Rol quitado a " + user.getName() + ".");
        } catch (UserEmptyException | UserNoFoundException e) {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe.");
        }
        return "redirect:/app/users";
    }

}
