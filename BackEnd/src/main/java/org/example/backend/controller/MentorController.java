package org.example.backend.controller;

import org.example.backend.dto.MentorRequest;
import org.example.backend.dto.MentorResponse;
import org.example.backend.service.IMentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
public class MentorController {

    private final IMentorService mentorService;

    /**
     * Endpoint lấy danh sách tất cả người hướng dẫn (có phân trang và tìm kiếm).
     * 
     * @param name Tên để tìm kiếm.
     * @param department Phòng ban để lọc.
     * @param page Chỉ số trang.
     * @param size Kích thước trang.
     * @param sortBy Trường sắp xếp.
     * @return ResponseEntity chứa trang danh sách người hướng dẫn.
     */
    @GetMapping
    public ResponseEntity<Page<MentorResponse>> getAllMentors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(mentorService.getAllMentors(name, department, page, size, sortBy));
    }

    /**
     * Endpoint lấy thông tin một người hướng dẫn cụ thể.
     * 
     * @param id ID của người hướng dẫn.
     * @return ResponseEntity chứa thông tin chi tiết.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MentorResponse> getMentorById(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.getMentorById(id));
    }

    /**
     * Endpoint tạo mới người hướng dẫn.
     * 
     * @param request Thông tin cần thiết để tạo mentor.
     * @return ResponseEntity chứa thông tin mentor vừa tạo.
     */
    @PostMapping
    public ResponseEntity<MentorResponse> createMentor(@RequestBody MentorRequest request) {
        return ResponseEntity.ok(mentorService.createMentor(request));
    }

    /**
     * Endpoint cập nhật thông tin người hướng dẫn.
     * 
     * @param id ID của mentor cần cập nhật.
     * @param request Thông tin mới.
     * @return ResponseEntity chứa thông tin mentor sau khi cập nhật.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MentorResponse> updateMentor(@PathVariable Long id, @RequestBody MentorRequest request) {
        return ResponseEntity.ok(mentorService.updateMentor(id, request));
    }

    /**
     * Endpoint xóa người hướng dẫn.
     * 
     * @param id ID của mentor cần xóa.
     * @return ResponseEntity trả về trạng thái 200 OK sau khi xóa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMentor(@PathVariable Long id) {
        mentorService.deleteMentor(id);
        return ResponseEntity.ok().build();
    }
}
