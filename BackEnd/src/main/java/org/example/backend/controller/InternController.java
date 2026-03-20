package org.example.backend.controller;

import org.example.backend.dto.InternRequest;
import org.example.backend.dto.InternResponse;
import org.example.backend.service.IInternService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interns")
@RequiredArgsConstructor
public class InternController {

    private final IInternService internService;

    /**
     * Endpoint lấy danh sách thực tập sinh (có phân trang và tìm kiếm).
     * 
     * @param name Tên để tìm kiếm.
     * @param university Trường đại học để tìm kiếm.
     * @param mentorId ID người hướng dẫn để lọc.
     * @param status Trạng thái để lọc.
     * @param page Chỉ số trang.
     * @param size Kích thước trang.
     * @param sortBy Trường sắp xếp.
     * @return ResponseEntity chứa trang danh sách thực tập sinh.
     */
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<InternResponse>> getAllInterns(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String university,
            @RequestParam(required = false) Long mentorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(internService.getAllInterns(name, university, mentorId, status, page, size, sortBy));
    }

    /**
     * Endpoint lấy thông tin chi tiết một thực tập sinh.
     * 
     * @param id ID thực tập sinh.
     * @return ResponseEntity chứa thông tin thực tập sinh.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InternResponse> getInternById(@PathVariable Long id) {
        return ResponseEntity.ok(internService.getInternById(id));
    }

    /**
     * Endpoint tạo mới thực tập sinh (Chỉ dành cho ADMIN).
     * 
     * @param request Thông tin thực tập sinh.
     * @return ResponseEntity chứa thông tin thực tập sinh vừa tạo.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InternResponse> createIntern(@jakarta.validation.Valid @RequestBody InternRequest request) {
        System.out.println("📥 InternController: Target hit - Creating intern for email: " + request.getEmail());
        return ResponseEntity.ok(internService.createIntern(request));
    }

    /**
     * Endpoint cập nhật thông tin thực tập sinh (Chỉ dành cho ADMIN).
     * 
     * @param id ID thực tập sinh cần cập nhật.
     * @param request Thông tin mới.
     * @return ResponseEntity chứa thông tin sau cập nhật.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InternResponse> updateIntern(@PathVariable Long id, @jakarta.validation.Valid @RequestBody InternRequest request) {
        return ResponseEntity.ok(internService.updateIntern(id, request));
    }

    /**
     * Endpoint xóa thực tập sinh (Chỉ dành cho ADMIN).
     * 
     * @param id ID thực tập sinh cần xóa.
     * @return ResponseEntity trả về trạng thái thành công.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteIntern(@PathVariable Long id) {
        internService.deleteIntern(id);
        return ResponseEntity.ok().build();
    }
}
