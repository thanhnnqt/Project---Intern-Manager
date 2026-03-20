package org.example.backend.controller;

import org.example.backend.dto.TaskRequest;
import org.example.backend.dto.TaskResponse;
import org.example.backend.service.ITaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;

    /**
     * Endpoint lấy danh sách các nhiệm vụ có lọc và phân trang.
     * 
     * @param title Tiêu đề để tìm kiếm.
     * @param status Trạng thái để lọc.
     * @param internId ID thực tập sinh để lọc.
     * @param mentorId ID người hướng dẫn để lọc.
     * @param pageable Thông tin phân trang và sắp xếp.
     * @return ResponseEntity chứa trang danh sách nhiệm vụ.
     */
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long internId,
            @RequestParam(required = false) Long mentorId,
            Pageable pageable) {
        System.out.println("📥 TaskController: Fetching tasks with params - title: " + title + ", status: " + status);
        return ResponseEntity.ok(taskService.getAllTasks(title, status, internId, mentorId, pageable));
    }

    /**
     * Endpoint lấy thông tin chi tiết một nhiệm vụ.
     * 
     * @param id ID của nhiệm vụ.
     * @return ResponseEntity chứa thông tin nhiệm vụ.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    /**
     * Endpoint tạo nhiệm vụ mới.
     * 
     * @param request Thông tin nhiệm vụ cần tạo.
     * @return ResponseEntity chứa thông tin nhiệm vụ vừa tạo.
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        System.out.println("📥 TaskController: Creating task - Title: " + request.getTitle() + ", InternId: " + request.getInternId() + ", MentorId: " + request.getMentorId());
        return ResponseEntity.ok(taskService.createTask(request));
    }

    /**
     * Endpoint cập nhật một nhiệm vụ hiện có.
     * 
     * @param id ID nhiệm vụ cần cập nhật.
     * @param request Thông tin cập nhật mới.
     * @return ResponseEntity chứa thông tin nhiệm vụ sau cập nhật.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    /**
     * Endpoint xóa một nhiệm vụ.
     * 
     * @param id ID nhiệm vụ cần xóa.
     * @return ResponseEntity trả về trạng thái thành công.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
