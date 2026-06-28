package com.propsightai.security;

public interface JwtService {
    String generateToken(String email);
    String extractEmail(String token);
    boolean isValid(String token, String email);
    String generateRefreshToken(String email);
}
