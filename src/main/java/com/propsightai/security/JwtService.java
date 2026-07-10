package com.propsightai.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {

    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String extractEmail(String token);

    String extractRole(String token);

    Date extractExpiration(String token);

    boolean isExpired(String token);

    boolean isValid(String token, UserDetails userDetails);
}