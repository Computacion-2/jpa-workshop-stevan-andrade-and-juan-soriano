package com.example.controller.mvc;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.security.AppUserDetails;
import com.example.service.PermissionService;
import com.example.service.RoleService;
import com.example.service.UserService;

@Controller
public class HomeMVCController {

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    public HomeMVCController(UserService userService, RoleService roleService, PermissionService permissionService) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal AppUserDetails currentUser, Model model) {
        model.addAttribute("myRole", currentUser.getRoleName());
        model.addAttribute("myPermissions", currentUser.getPermissions());
        model.addAttribute("userCount", userService.findAll().size());
        model.addAttribute("roleCount", roleService.findAll().size());
        model.addAttribute("permissionCount", permissionService.findAll().size());
        return "home/index";
    }

    @RequestMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
