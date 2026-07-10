package com.propsightai.Service;
import com.propsightai.Dto.AuthResponse;
import com.propsightai.Dto.LoginRequest;
import com.propsightai.Dto.SignupRequest;

public interface AuthService {

    String signup(SignupRequest request);
    AuthResponse login(LoginRequest request);

    void verifyEmail(String token);

    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);

    // Rotate and return new tokens
    AuthResponse refreshToken(String refreshToken);

    // Revoke a refresh token
    void revokeRefreshToken(String refreshToken);
}