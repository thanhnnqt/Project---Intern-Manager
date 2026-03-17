package org.example.backend.controller;

import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.GoogleLoginRequest;
import org.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest googleRequest) {
        System.out.println("Received Google login request for token: " + (googleRequest.getIdToken() != null ? "present" : "absent"));
        return ResponseEntity.ok(authService.loginWithGoogle(googleRequest));
    }
}
