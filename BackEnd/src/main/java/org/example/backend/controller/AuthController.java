package org.example.backend.controller;

import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.GoogleLoginRequest;
import org.example.backend.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    /**
     * Endpoint xử lý đăng nhập bằng tài khoản hệ thống (username/password).
     * 
     * @param loginRequest Thông tin đăng nhập của người dùng.
     * @return ResponseEntity chứa kết quả xác thực và JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    /**
     * Endpoint xử lý đăng nhập bằng tài khoản Google.
     * 
     * @param googleRequest Chứa Google ID Token.
     * @return ResponseEntity chứa kết quả xác thực và JWT token.
     */
    @PostMapping("/google-login")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest googleRequest) {
        System.out.println("Received Google login request for token: " + (googleRequest.getIdToken() != null ? "present" : "absent"));
        return ResponseEntity.ok(authService.loginWithGoogle(googleRequest));
    }
}
