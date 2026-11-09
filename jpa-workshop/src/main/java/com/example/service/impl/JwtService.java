package com.example.service.impl;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.security.AppUserDetails;
import com.example.service.interf.IJwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService implements IJwtService {

    static final String CLAIM_USER_ID = "userId";
    static final String CLAIM_NAME = "name";
    static final String CLAIM_ROLE = "role";
    static final String CLAIM_AUTHORITIES = "authorities";

    private final SecretKey signInKey;
    private final long expirationTime;

    public JwtService(@Value("${app.security.jwt.secret-key}") String secretKey,
                      @Value("${app.security.jwt.expiration-time}") long expirationTime) {
        this.signInKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.expirationTime = expirationTime;
    }

    @Override
    public String generateToken(AppUserDetails user) {
        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim(CLAIM_USER_ID, user.getId())
                .claim(CLAIM_NAME, user.getName())
                .claim(CLAIM_ROLE, user.getRoleName())
                .claim(CLAIM_AUTHORITIES, authorities)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationTime))
                .signWith(signInKey)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public List<SimpleGrantedAuthority> extractAuthorities(String token) {
        List<?> authorities = extractClaim(token, claims -> claims.get(CLAIM_AUTHORITIES, List.class));
        if (authorities == null) {
            return List.of();
        }
        return authorities.stream()
                .map(String::valueOf)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public UserDetails getUserDetailsFromToken(String token) {
        return new User(extractUsername(token), "", extractAuthorities(token));
    }

    @Override
    public long getExpirationTime() {
        return expirationTime;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signInKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
