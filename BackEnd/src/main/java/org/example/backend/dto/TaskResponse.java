package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime deadline;
    private Long internId;
    private Long mentorId;
    private String internName;
    private String mentorName;
}
