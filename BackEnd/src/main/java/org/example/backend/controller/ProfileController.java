package org.example.backend.controller;

import org.example.backend.dto.ProfileResponse;
import org.example.backend.dto.UpdatePasswordRequest;
import org.example.backend.dto.UpdateProfileRequest;
import org.example.backend.service.IProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final IProfileService profileService;

    /**
     * Endpoint lấy thông tin hồ sơ cá nhân của người dùng hiện tại.
     * 
     * @return ResponseEntity chứa thông tin hồ sơ.
     */
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(profileService.getCurrentProfile());
    }

    /**
     * Endpoint cập nhật thông tin hồ sơ cá nhân.
     * 
     * @param request Thông tin hồ sơ mới.
     * @return ResponseEntity chứa thông tin hồ sơ sau cập nhật.
     */
    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(request));
    }

    /**
     * Endpoint thay đổi mật khẩu.
     * 
     * @param request Chứa mật khẩu cũ và mật khẩu mới.
     * @return ResponseEntity trả về thông báo thành công.
     */
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        profileService.updatePassword(request);
        return ResponseEntity.ok().build();
    }
}
