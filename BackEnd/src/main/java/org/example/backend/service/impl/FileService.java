package org.example.backend.service.impl;

import org.example.backend.dto.FileResponse;
import org.example.backend.entity.File;
import org.example.backend.repository.FileRepository;
import org.example.backend.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService implements IFileService {

    private final FileRepository fileRepository;

    /**
     * Lấy danh sách tệp tin liên quan đến một thực tập sinh.
     * 
     * @param internId ID của thực tập sinh.
     * @return List<FileResponse> Danh sách thông tin tệp tin.
     */
    public List<FileResponse> getFilesByInternId(Long internId) {
        return fileRepository.findByInternId(internId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách tệp tin đính kèm trong một task.
     * 
     * @param taskId ID của task.
     * @return List<FileResponse> Danh sách thông tin tệp tin.
     */
    public List<FileResponse> getFilesByTaskId(Long taskId) {
        return fileRepository.findByTaskId(taskId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FileResponse mapToResponse(File file) {
        return FileResponse.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .filePath(file.getFilePath())
                .taskId(file.getTask() != null ? file.getTask().getId() : null)
                .taskTitle(file.getTask() != null ? file.getTask().getTitle() : null)
                .build();
    }
}
