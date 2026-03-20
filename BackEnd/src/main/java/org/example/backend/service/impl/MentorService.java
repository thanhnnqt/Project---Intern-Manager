package org.example.backend.service.impl;

import org.example.backend.dto.MentorRequest;
import org.example.backend.dto.MentorResponse;
import org.example.backend.entity.Account;
import org.example.backend.entity.Mentor;
import org.example.backend.repository.InternRepository;
import org.example.backend.repository.AccountRepository;
import org.example.backend.repository.MentorRepository;
import org.example.backend.service.IMentorService;
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
public class MentorService implements IMentorService {

    private final AccountRepository accountRepository;
    private final MentorRepository mentorRepository;
    private final InternRepository internRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lấy danh sách tất cả các người hướng dẫn (Mentor) có phân trang và lọc theo tiêu chí.
     * 
     * @param name (Tùy chọn) Tên người hướng dẫn.
     * @param department (Tùy chọn) Phòng ban.
     * @param page Số trang hiện tại.
     * @param size Số lượng bản ghi trên một trang.
     * @param sortBy Trường dùng để sắp xếp.
     * @return Page<MentorResponse> Trang danh sách người hướng dẫn.
     */
    public Page<MentorResponse> getAllMentors(String name, String department, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());

        Specification<Mentor> spec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%"));
            }

            if (StringUtils.hasText(department)) {
                predicates.add(cb.like(cb.lower(root.get("department")), "%" + department.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return mentorRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    /**
     * Lấy thông tin chi tiết một người hướng dẫn theo ID.
     * 
     * @param id ID của người hướng dẫn.
     * @return MentorResponse Thông tin chi tiết người hướng dẫn.
     */
    public MentorResponse getMentorById(Long id) {
        Mentor mentor = mentorRepository.findById(id).orElseThrow(() -> new RuntimeException("Mentor not found"));
        return mapToResponse(mentor);
    }

    /**
     * Tạo mới một người hướng dẫn.
     * Tự động tạo tài khoản đăng nhập với mật khẩu mặc định.
     * 
     * @param request Thông tin người hướng dẫn cần tạo.
     * @return MentorResponse Thông tin người hướng dẫn vừa tạo.
     */
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

    /**
     * Cập nhật thông tin của người hướng dẫn hiện có.
     * 
     * @param id ID của người hướng dẫn cần cập nhật.
     * @param request Thông tin cập nhật mới.
     * @return MentorResponse Thông tin người hướng dẫn sau cập nhật.
     */
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

    /**
     * Xóa một người hướng dẫn và tài khoản liên quan khỏi hệ thống.
     * 
     * @param id ID của người hướng dẫn cần xóa.
     */
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
