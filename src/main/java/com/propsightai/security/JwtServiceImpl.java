package com.propsightai.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private static final long ACCESS_TOKEN_TIME = 1000L * 60 * 15;
    private static final long REFRESH_TOKEN_TIME = 1000L * 60 * 60 * 24 * 7;

    private Key getSigningKey() {
        byte[] keyBytes;

        try {
            keyBytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException ex) {
            keyBytes = secret.getBytes();
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {

        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");   // Default role

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                // Setting token expiration to 24 Hours for stable development testing timelines
.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256 )
                .compact();
    }

    private Claims parseClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    @Override
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    @Override
    public Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    @Override
    public boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public boolean isValid(String token, UserDetails userDetails) {

        try {

            String email = extractEmail(token);

            return email.equals(userDetails.getUsername())
                    && !isExpired(token);

        } catch (ExpiredJwtException |
                 UnsupportedJwtException |
                 MalformedJwtException |
                 SecurityException |
                 IllegalArgumentException ex) {

            return false;
        }
    }
}