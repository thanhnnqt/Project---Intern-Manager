package org.example.backend.controller;

import org.example.backend.dto.CommentRequest;
import org.example.backend.dto.CommentResponse;
import org.example.backend.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    /**
     * Endpoint lấy danh sách bình luận của một task.
     * 
     * @param taskId ID của task.
     * @return ResponseEntity chứa danh sách bình luận.
     */
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@RequestParam Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId));
    }

    /**
     * Endpoint thêm bình luận mới.
     * 
     * @param request Thông tin bình luận cần thêm.
     * @return ResponseEntity chứa thông tin bình luận vừa tạo.
     */
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(request));
    }

    /**
     * Endpoint xóa bình luận.
     * 
     * @param id ID của bình luận cần xóa.
     * @return ResponseEntity trả về trạng thái thành công (200 OK).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}
