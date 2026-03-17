package org.example.backend.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long taskId;
    private Long userId;
}
