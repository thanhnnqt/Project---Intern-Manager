package org.example.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime deadline;
    private Long internId;
    private Long mentorId;
}
