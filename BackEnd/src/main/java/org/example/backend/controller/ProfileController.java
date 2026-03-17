package org.example.backend.controller;

import org.example.backend.dto.ProfileResponse;
import org.example.backend.dto.UpdatePasswordRequest;
import org.example.backend.dto.UpdateProfileRequest;
import org.example.backend.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(profileService.getCurrentProfile());
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(request));
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        profileService.updatePassword(request);
        return ResponseEntity.ok().build();
    }
}
