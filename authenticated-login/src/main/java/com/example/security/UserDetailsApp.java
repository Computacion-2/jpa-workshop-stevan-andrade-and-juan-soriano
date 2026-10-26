
package com.example.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.model.Role;
import com.example.model.RolePermission;
import com.example.model.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDetailsApp implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        Role role = user.getRole();

        if (role == null) {
            return authorities;
        }

        authorities.add(new SecurityAuthority(role));

        if (role.getRolePermissions() != null) {
            role.getRolePermissions()
                    .stream()
                    .map(RolePermission::getPermission)
                    .filter(permission -> permission != null)
                    .map(SecurityAuthority::new)
                    .forEach(authorities::add);
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
