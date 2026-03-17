package org.example.backend.dto;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String idToken;
    private String email;
    private String name;
    private String picture;
    private String sub;
}
