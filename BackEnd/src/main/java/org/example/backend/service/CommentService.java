package org.example.backend.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private InternRepository internRepository;

    public List<CommentResponse> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

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

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    private CommentResponse mapToResponse(Comment comment) {
        Account account = comment.getAccount();
        String name = "N/A";
        String avatar = null;
        
        if (account.getRole() == Account.Role.MENTOR) {
            mentorRepository.findByAccountUsername(account.getUsername()).ifPresent(m -> {
                // name is final if assigned in ifPresent, so we use a trick or just use variables
            });
            // Re-fetch to be safe or use a helper
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
            name = account.getUsername(); // Admin or other
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
