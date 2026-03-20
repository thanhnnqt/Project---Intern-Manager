package org.example.backend.controller;

import org.example.backend.dto.FileResponse;
import org.example.backend.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final IFileService fileService;

    /**
     * Endpoint lấy danh sách tệp tin. 
     * Có thể lọc theo internId hoặc taskId.
     * 
     * @param internId (Tùy chọn) ID của thực tập sinh.
     * @param taskId (Tùy chọn) ID của task.
     * @return ResponseEntity chứa danh sách tệp tin phù hợp.
     */
    @GetMapping
    public ResponseEntity<List<FileResponse>> getFiles(
            @RequestParam(required = false) Long internId,
            @RequestParam(required = false) Long taskId) {
        if (internId != null) {
            return ResponseEntity.ok(fileService.getFilesByInternId(internId));
        }
        if (taskId != null) {
            return ResponseEntity.ok(fileService.getFilesByTaskId(taskId));
        }
        return ResponseEntity.ok(List.of());
    }
}
