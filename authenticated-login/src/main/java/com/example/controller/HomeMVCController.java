package com.example.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.service.interf.IPermissionService;
import com.example.service.interf.IRoleService;
import com.example.service.interf.IUserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeMVCController {

    private final IUserService userService;
    private final IRoleService roleService;
    private final IPermissionService permissionService;

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        List<String> granted = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        model.addAttribute("myRoles", granted.stream()
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .toList());
        model.addAttribute("myPermissions", granted.stream()
                .filter(authority -> !authority.startsWith("ROLE_"))
                .toList());

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
