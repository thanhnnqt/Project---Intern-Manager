package org.example.backend.service.impl;

import org.example.backend.dto.CommentRequest;
import org.example.backend.dto.CommentResponse;
import org.example.backend.entity.Comment;
import org.example.backend.entity.Task;
import org.example.backend.entity.Account;
import org.example.backend.entity.Mentor;
import org.example.backend.entity.Intern;
import org.example.backend.repository.CommentRepository;
import org.example.backend.repository.TaskRepository;
import org.example.backend.repository.AccountRepository;
import org.example.backend.repository.MentorRepository;
import org.example.backend.repository.InternRepository;
import org.example.backend.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final MentorRepository mentorRepository;
    private final InternRepository internRepository;

    /**
     * Lấy danh sách bình luận theo ID của task.
     * Sắp xếp theo thời gian tạo giảm dần (mới nhất lên đầu).
     * 
     * @param taskId ID của task cần lấy bình luận.
     * @return List<CommentResponse> Danh sách các bình luận đã được ánh xạ sang DTO.
     */
    public List<CommentResponse> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Thêm một bình luận mới vào task.
     * 
     * @param request Chứa nội dung bình luận, ID task và ID tài khoản người dùng.
     * @return CommentResponse Thông tin bình luận vừa tạo.
     */
    public CommentResponse addComment(CommentRequest request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        Account account = accountRepository.findById(request.getUserId()) // request.getUserId() now refers to accountId
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        Comment comment = Comment.builder()
                .content(request.getContent())
                .task(task)
                .account(account)
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponse(commentRepository.save(comment));
    }

    /**
     * Xóa một bình luận theo ID.
     * 
     * @param id ID của bình luận cần xóa.
     */
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    private CommentResponse mapToResponse(Comment comment) {
        Account account = comment.getAccount();
        String name = "N/A";
        String avatar = null;
        
        if (account.getRole() == Account.Role.MENTOR) {
            mentorRepository.findByAccountUsername(account.getUsername()).ifPresent(m -> {
            });
            Mentor mentor = mentorRepository.findByAccountUsername(account.getUsername()).orElse(null);
            if (mentor != null) {
                name = mentor.getFullName();
                avatar = mentor.getAvatar();
            }
        } else if (account.getRole() == Account.Role.USER) {
            Intern intern = internRepository.findByAccountUsername(account.getUsername()).orElse(null);
            if (intern != null) {
                name = intern.getFullName();
                avatar = intern.getAvatar();
            }
        } else {
            name = account.getUsername();
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userName(name)
                .userRole(account.getRole().name())
                .userAvatar(avatar)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
