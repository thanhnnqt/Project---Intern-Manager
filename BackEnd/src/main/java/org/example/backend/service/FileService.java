package org.example.backend.service;

import org.example.backend.dto.FileResponse;
import org.example.backend.entity.File;
import org.example.backend.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public List<FileResponse> getFilesByInternId(Long internId) {
        return fileRepository.findByInternId(internId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

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
