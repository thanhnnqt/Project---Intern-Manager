package org.example.backend.service;

import org.example.backend.dto.GoogleLoginRequest;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.LoginResponse;

public interface IAuthService {
    LoginResponse authenticateUser(LoginRequest loginRequest);
    LoginResponse loginWithGoogle(GoogleLoginRequest googleRequest);
}
