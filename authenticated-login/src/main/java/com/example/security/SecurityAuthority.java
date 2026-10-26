package com.example.security;

import org.springframework.security.core.GrantedAuthority;

import com.example.model.Permission;
import com.example.model.Role;

public class SecurityAuthority implements GrantedAuthority {
    
    private final String authority;

    public SecurityAuthority(Permission permission) {
        this.authority = permission.getName();
    }

    public SecurityAuthority(Role role) {
        this.authority = "ROLE_" + role.getName();
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    

}
