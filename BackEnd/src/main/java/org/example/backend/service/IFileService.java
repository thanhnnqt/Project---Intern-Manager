package org.example.backend.service;

import org.example.backend.dto.FileResponse;
import java.util.List;

public interface IFileService {
    List<FileResponse> getFilesByInternId(Long internId);
    List<FileResponse> getFilesByTaskId(Long taskId);
}
