package org.example.backend.service;

import org.example.backend.dto.ProfileResponse;
import org.example.backend.dto.UpdatePasswordRequest;
import org.example.backend.dto.UpdateProfileRequest;
import org.example.backend.entity.Account;
import org.example.backend.entity.Mentor;
import org.example.backend.entity.Intern;
import org.example.backend.repository.AccountRepository;
import org.example.backend.repository.MentorRepository;
import org.example.backend.repository.InternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private InternRepository internRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ProfileResponse getCurrentProfile() {
        Account account = getCurrentAccount();
        if (account.getRole() == Account.Role.MENTOR) {
            Mentor mentor = mentorRepository.findByAccountUsername(account.getUsername())
                    .orElseThrow(() -> new RuntimeException("Mentor profile not found"));
            return mapMentorToResponse(mentor);
        } else {
            Intern intern = internRepository.findByAccountUsername(account.getUsername())
                    .orElseThrow(() -> new RuntimeException("Intern profile not found"));
            return mapInternToResponse(intern);
        }
    }

    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        Account account = getCurrentAccount();
        if (account.getRole() == Account.Role.MENTOR) {
            Mentor mentor = mentorRepository.findByAccountUsername(account.getUsername())
                    .orElseThrow(() -> new RuntimeException("Mentor profile not found"));
            
            if (request.getFullName() != null) mentor.setFullName(request.getFullName());
            if (request.getEmail() != null) mentor.setEmail(request.getEmail());
            if (request.getPhone() != null) mentor.setPhone(request.getPhone());
            if (request.getAvatar() != null) mentor.setAvatar(request.getAvatar());
            
            return mapMentorToResponse(mentorRepository.save(mentor));
        } else {
            Intern intern = internRepository.findByAccountUsername(account.getUsername())
                    .orElseThrow(() -> new RuntimeException("Intern profile not found"));
            
            if (request.getFullName() != null) intern.setFullName(request.getFullName());
            if (request.getEmail() != null) intern.setEmail(request.getEmail());
            if (request.getPhone() != null) intern.setPhone(request.getPhone());
            if (request.getAvatar() != null) intern.setAvatar(request.getAvatar());
            
            return mapInternToResponse(internRepository.save(intern));
        }
    }

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
