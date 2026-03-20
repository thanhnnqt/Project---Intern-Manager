package org.example.backend.service.impl;

import org.example.backend.dto.InternRequest;
import org.example.backend.dto.InternResponse;
import org.example.backend.entity.Intern;
import org.example.backend.entity.Account;
import org.example.backend.entity.Mentor;
import org.example.backend.repository.InternRepository;
import org.example.backend.repository.AccountRepository;
import org.example.backend.repository.MentorRepository;
import org.example.backend.service.IInternService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@Service
@org.springframework.transaction.annotation.Transactional
@RequiredArgsConstructor
public class InternService implements IInternService {

    private final InternRepository internRepository;
    private final AccountRepository accountRepository;
    private final MentorRepository mentorRepository;
    private final PasswordEncoder encoder;

    /**
     * Lấy danh sách thực tập sinh có phân trang và lọc theo tiêu chí.
     * 
     * @param name (Tùy chọn) Tên thực tập sinh.
     * @param university (Tùy chọn) Tên trường đại học.
     * @param mentorId (Tùy chọn) ID của người hướng dẫn.
     * @param status (Tùy chọn) Trạng thái thực tập.
     * @param page Số trang hiện tại.
     * @param size Số lượng bản ghi trên một trang.
     * @param sortBy Trường dùng để sắp xếp.
     * @return Page<InternResponse> Trang danh sách thực tập sinh.
     */
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

    /**
     * Lấy thông tin chi tiết của một thực tập sinh theo ID.
     * 
     * @param id ID của thực tập sinh.
     * @return InternResponse Thông tin chi tiết thực tập sinh.
     */
    public InternResponse getInternById(Long id) {
        Intern intern = internRepository.findById(id).orElseThrow(() -> new RuntimeException("Intern not found"));
        return mapToResponse(intern);
    }

    /**
     * Tạo mới một thực tập sinh.
     * Đồng thời tạo tài khoản đăng nhập (Account) cho thực tập sinh đó.
     * 
     * @param request Thông tin thực tập sinh cần tạo.
     * @return InternResponse Thông tin thực tập sinh vừa tạo.
     */
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

    /**
     * Cập nhật thông tin thực tập sinh hiện có.
     * 
     * @param id ID của thực tập sinh cần cập nhật.
     * @param request Thông tin cập nhật mới.
     * @return InternResponse Thông tin thực tập sinh sau khi cập nhật.
     */
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

    /**
     * Xóa một thực tập sinh khỏi hệ thống.
     * 
     * @param id ID của thực tập sinh cần xóa.
     */
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
