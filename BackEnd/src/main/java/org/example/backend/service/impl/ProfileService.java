package org.example.backend.service.impl;

import org.example.backend.dto.ProfileResponse;
import org.example.backend.dto.UpdatePasswordRequest;
import org.example.backend.dto.UpdateProfileRequest;
import org.example.backend.entity.Account;
import org.example.backend.entity.Mentor;
import org.example.backend.entity.Intern;
import org.example.backend.repository.AccountRepository;
import org.example.backend.repository.MentorRepository;
import org.example.backend.repository.InternRepository;
import org.example.backend.service.IProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileService implements IProfileService {

    private final AccountRepository accountRepository;
    private final MentorRepository mentorRepository;
    private final InternRepository internRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lấy thông tin hồ sơ của người dùng hiện tại đang đăng nhập.
     * Tự động xác định loại người dùng (Mentor, Intern hoặc Admin) để trả về dữ liệu phù hợp.
     * 
     * @return ProfileResponse Thông tin hồ sơ chi tiết.
     */
    public ProfileResponse getCurrentProfile() {
        Account account = getCurrentAccount();
        
        // Try to find Mentor profile
        var mentorOpt = mentorRepository.findByAccountUsername(account.getUsername());
        if (mentorOpt.isPresent()) {
            return mapMentorToResponse(mentorOpt.get());
        }
        
        // Try to find Intern profile
        var internOpt = internRepository.findByAccountUsername(account.getUsername());
        if (internOpt.isPresent()) {
            return mapInternToResponse(internOpt.get());
        }
        
        // Fallback for ADMIN or users without specific profile
        return ProfileResponse.builder()
                .username(account.getUsername())
                .role(account.getRole())
                // ADMINs might not have these fields in a separate table, so we use defaults or account info
                .fullName("Administrator") 
                .email(account.getUsername()) // Use username as email fallback if email is not in Account
                .build();
    }

    /**
     * Cập nhật thông tin hồ sơ cá nhân.
     * Cho phép cập nhật họ tên, email, số điện thoại và ảnh đại diện.
     * 
     * @param request Chứa các thông tin mới cần cập nhật.
     * @return ProfileResponse Thông tin hồ sơ sau khi đã cập nhật.
     */
    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        Account account = getCurrentAccount();
        
        var mentorOpt = mentorRepository.findByAccountUsername(account.getUsername());
        if (mentorOpt.isPresent()) {
            Mentor mentor = mentorOpt.get();
            if (request.getFullName() != null) mentor.setFullName(request.getFullName());
            if (request.getEmail() != null) mentor.setEmail(request.getEmail());
            if (request.getPhone() != null) mentor.setPhone(request.getPhone());
            if (request.getAvatar() != null) mentor.setAvatar(request.getAvatar());
            return mapMentorToResponse(mentorRepository.save(mentor));
        }
        
        var internOpt = internRepository.findByAccountUsername(account.getUsername());
        if (internOpt.isPresent()) {
            Intern intern = internOpt.get();
            if (request.getFullName() != null) intern.setFullName(request.getFullName());
            if (request.getEmail() != null) intern.setEmail(request.getEmail());
            if (request.getPhone() != null) intern.setPhone(request.getPhone());
            if (request.getAvatar() != null) intern.setAvatar(request.getAvatar());
            return mapInternToResponse(internRepository.save(intern));
        }

        throw new RuntimeException("Không tìm thấy hồ sơ cá nhân để cập nhật cho tài khoản này.");
    }

    /**
     * Thay đổi mật khẩu cho người dùng hiện tại.
     * Yêu cầu xác nhận mật khẩu cũ trước khi đặt mật khẩu mới.
     * 
     * @param request Chứa mật khẩu cũ và mật khẩu mới.
     */
    public void updatePassword(UpdatePasswordRequest request) {
        Account account = getCurrentAccount();
        
        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }
        
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
    }

    private Account getCurrentAccount() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    private ProfileResponse mapMentorToResponse(Mentor mentor) {
        return ProfileResponse.builder()
                .id(mentor.getId())
                .username(mentor.getAccount().getUsername())
                .email(mentor.getEmail())
                .fullName(mentor.getFullName())
                .phone(mentor.getPhone())
                .avatar(mentor.getAvatar())
                .department(mentor.getDepartment())
                .position(mentor.getPosition())
                .role(mentor.getAccount().getRole())
                .build();
    }

    private ProfileResponse mapInternToResponse(Intern intern) {
        return ProfileResponse.builder()
                .id(intern.getId())
                .username(intern.getAccount().getUsername())
                .email(intern.getEmail())
                .fullName(intern.getFullName())
                .phone(intern.getPhone())
                .avatar(intern.getAvatar())
                .role(intern.getAccount().getRole())
                .university(intern.getUniversity())
                .major(intern.getMajor())
                .status(intern.getStatus())
                .build();
    }
}
