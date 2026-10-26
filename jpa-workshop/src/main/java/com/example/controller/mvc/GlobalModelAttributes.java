package com.example.controller.mvc;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.security.AppUserDetails;

@ControllerAdvice(annotations = Controller.class)
public class GlobalModelAttributes {

    @ModelAttribute("currentUsername")
    public String currentUsername() {
        Authentication auth = currentAuthentication();
        return auth == null ? null : auth.getName();
    }

    @ModelAttribute("currentName")
    public String currentName() {
        Authentication auth = currentAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUserDetails details) {
            return details.getName();
        }
        return null;
    }

    @ModelAttribute("authorities")
    public Set<String> authorities() {
        Authentication auth = currentAuthentication();
        if (auth == null) {
            return Collections.emptySet();
        }
        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    private Authentication currentAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return auth;
    }
}
