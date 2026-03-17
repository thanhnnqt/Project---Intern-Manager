package org.example.backend.service;

import org.example.backend.dto.MentorRequest;
import org.example.backend.dto.MentorResponse;
import org.example.backend.entity.Account;
import org.example.backend.entity.Mentor;
import org.example.backend.repository.InternRepository;
import org.example.backend.repository.AccountRepository;
import org.example.backend.repository.MentorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@org.springframework.transaction.annotation.Transactional
public class MentorService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private InternRepository internRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<MentorResponse> getAllMentors() {
        return mentorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public MentorResponse getMentorById(Long id) {
        Mentor mentor = mentorRepository.findById(id).orElseThrow(() -> new RuntimeException("Mentor not found"));
        return mapToResponse(mentor);
    }

    public MentorResponse createMentor(MentorRequest request) {
        Account account = Account.builder()
                .username(request.getEmail())
                .password(passwordEncoder.encode("mentor123")) // Default password
                .role(Account.Role.MENTOR)
                .build();
        // No need to save account independently, Mentor cascades persist

        Mentor mentor = Mentor.builder()
                .fullName(request.getName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .position(request.getPosition())
                .avatar(request.getAvatar())
                .account(account)
                .build();
        mentor = mentorRepository.save(mentor);
        return mapToResponse(mentor);
    }

    public MentorResponse updateMentor(Long id, MentorRequest request) {
        Mentor mentor = mentorRepository.findById(id).orElseThrow(() -> new RuntimeException("Mentor not found"));
        
        Account account = mentor.getAccount();
        account.setUsername(request.getEmail());
        accountRepository.save(account);

        mentor.setFullName(request.getName());
        mentor.setEmail(request.getEmail());
        mentor.setDepartment(request.getDepartment());
        mentor.setPosition(request.getPosition());
        mentor.setAvatar(request.getAvatar());
        mentor = mentorRepository.save(mentor);
        return mapToResponse(mentor);
    }

    public void deleteMentor(Long id) {
        Mentor mentor = mentorRepository.findById(id).orElseThrow(() -> new RuntimeException("Mentor not found"));
        mentorRepository.delete(mentor);
        // Account will be deleted via CascadeType.ALL if configured, or manually:
        accountRepository.delete(mentor.getAccount());
    }

    private MentorResponse mapToResponse(Mentor mentor) {
        long internCount = internRepository.countByMentorId(mentor.getId());
        return MentorResponse.builder()
                .id(mentor.getId())
                .name(mentor.getFullName())
                .email(mentor.getEmail())
                .department(mentor.getDepartment())
                .position(mentor.getPosition())
                .avatar(mentor.getAvatar())
                .internCount(internCount)
                .build();
    }
}
