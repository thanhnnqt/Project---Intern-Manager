package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private String filePath;
    private Long taskId;
    private String taskTitle;
}
