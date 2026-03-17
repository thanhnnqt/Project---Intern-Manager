package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private String userName;
    private String userRole;
    private String userAvatar;
    private LocalDateTime createdAt;
}
