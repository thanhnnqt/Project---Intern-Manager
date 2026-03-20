package org.example.backend.service;

import org.example.backend.dto.ProfileResponse;
import org.example.backend.dto.UpdatePasswordRequest;
import org.example.backend.dto.UpdateProfileRequest;

public interface IProfileService {
    ProfileResponse getCurrentProfile();
    ProfileResponse updateProfile(UpdateProfileRequest request);
    void updatePassword(UpdatePasswordRequest request);
}
