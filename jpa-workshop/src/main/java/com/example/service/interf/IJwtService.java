package com.example.service.interf;

import java.util.Date;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.security.AppUserDetails;

public interface IJwtService {

    String generateToken(AppUserDetails user);

    boolean isTokenValid(String token);

    String extractUsername(String token);

    List<SimpleGrantedAuthority> extractAuthorities(String token);

    Date extractExpiration(String token);

    UserDetails getUserDetailsFromToken(String token);

    long getExpirationTime();
}
