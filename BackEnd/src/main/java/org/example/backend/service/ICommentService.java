package org.example.backend.service;

import org.example.backend.dto.CommentRequest;
import org.example.backend.dto.CommentResponse;
import java.util.List;

public interface ICommentService {
    List<CommentResponse> getCommentsByTaskId(Long taskId);
    CommentResponse addComment(CommentRequest request);
    void deleteComment(Long id);
}
