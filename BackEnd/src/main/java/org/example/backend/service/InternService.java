package org.example.backend.service;

import org.example.backend.dto.InternRequest;
import org.example.backend.dto.InternResponse;
import org.example.backend.entity.Intern;
import org.example.backend.entity.Account;
import org.example.backend.entity.Mentor;
import org.example.backend.repository.InternRepository;
import org.example.backend.repository.AccountRepository;
import org.example.backend.repository.MentorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.util.StringUtils;

@Service
@org.springframework.transaction.annotation.Transactional
public class InternService {

    @Autowired
    private InternRepository internRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private PasswordEncoder encoder;

    public Page<InternResponse> getAllInterns(String name, String university, Long mentorId, String status, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        
        Specification<Intern> spec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(university)) {
                predicates.add(cb.like(cb.lower(root.get("university")), "%" + university.toLowerCase() + "%"));
            }
            
            if (mentorId != null) {
                predicates.add(cb.equal(root.get("mentor").get("id"), mentorId));
            }

            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return internRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    public InternResponse getInternById(Long id) {
        Intern intern = internRepository.findById(id).orElseThrow(() -> new RuntimeException("Intern not found"));
        return mapToResponse(intern);
    }

    public InternResponse createIntern(InternRequest request) {
        Account account = Account.builder()
                .username(request.getEmail()) 
                .password(encoder.encode(request.getPassword())) 
                .role(Account.Role.USER)
                .build();
        // No need to save account independently, Intern cascades persist

        Mentor mentor = null;
        if (request.getMentorId() != null) {
            mentor = mentorRepository.findById(request.getMentorId()).orElse(null);
        }

        Intern intern = Intern.builder()
                .fullName(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .avatar(request.getAvatar())
                .university(request.getUniversity())
                .major(request.getMajor())
                .status(request.getStatus())
                .account(account)
                .mentor(mentor)
                .build();

        intern = internRepository.save(intern);
        return mapToResponse(intern);
    }

    public InternResponse updateIntern(Long id, InternRequest request) {
        Intern intern = internRepository.findById(id).orElseThrow(() -> new RuntimeException("Intern not found"));
        
        Account account = intern.getAccount();
        account.setUsername(request.getEmail());
        accountRepository.save(account);

        Mentor mentor = null;
        if (request.getMentorId() != null) {
            mentor = mentorRepository.findById(request.getMentorId()).orElse(null);
        }

        intern.setFullName(request.getName());
        intern.setEmail(request.getEmail());
        intern.setPhone(request.getPhone());
        intern.setAvatar(request.getAvatar());
        intern.setUniversity(request.getUniversity());
        intern.setMajor(request.getMajor());
        intern.setStatus(request.getStatus());
        intern.setMentor(mentor);

        intern = internRepository.save(intern);
        return mapToResponse(intern);
    }

    public void deleteIntern(Long id) {
        Intern intern = internRepository.findById(id).orElseThrow(() -> new RuntimeException("Intern not found"));
        internRepository.delete(intern);
    }

    private InternResponse mapToResponse(Intern intern) {
        return InternResponse.builder()
                .id(intern.getId())
                .name(intern.getFullName())
                .email(intern.getEmail())
                .phone(intern.getPhone())
                .avatar(intern.getAvatar())
                .university(intern.getUniversity())
                .major(intern.getMajor())
                .status(intern.getStatus())
                .mentorId(intern.getMentor() != null ? intern.getMentor().getId() : null)
                .mentorName(intern.getMentor() != null ? intern.getMentor().getFullName() : null)
                .accountId(intern.getAccount() != null ? intern.getAccount().getId() : null)
                .build();
    }
}
