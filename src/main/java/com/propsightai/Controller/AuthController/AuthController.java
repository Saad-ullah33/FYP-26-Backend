package com.propsightai.Controller.AuthController;

import com.propsightai.Dto.AuthResponse;
import com.propsightai.Dto.LoginRequest;
import com.propsightai.Dto.RefreshRequest;
import com.propsightai.Dto.SignupRequest;
import com.propsightai.Model.User;
import com.propsightai.Repository.UserRepository;
import com.propsightai.Role.Role;
import com.propsightai.Service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @RequestBody SignupRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody RefreshRequest request) {

        return ResponseEntity.ok(
                authService.refreshToken(
                        request.getRefreshToken()
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody RefreshRequest request) {

        authService.revokeRefreshToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                "Logout successful."
        );
    }

    @GetMapping("/verify-email")
    public void verifyEmail(
            @RequestParam String token,
            HttpServletResponse response) throws IOException {

        authService.verifyEmail(token);

        response.sendRedirect(
                "http://localhost:5173/login?verified=true"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/promote/{id}")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUserType(Role.ADMIN);

        userRepository.save(user);

        return ResponseEntity.ok("User promoted to Admin");
    }

}