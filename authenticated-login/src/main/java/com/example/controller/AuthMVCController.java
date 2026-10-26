package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.exception.RegistrationException;
import com.example.service.interf.IAuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mvc/auth")
@RequiredArgsConstructor
public class AuthMVCController {

    private final IAuthService authService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           RedirectAttributes redirectAttributes) {
        try {
            authService.register(name, email, password, confirmPassword);
            redirectAttributes.addFlashAttribute("success",
                    "Cuenta creada. Ya puedes iniciar sesión como " + name.trim() + ".");
            return "redirect:/mvc/auth/login";
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/mvc/auth/register";
        }
    }
}
