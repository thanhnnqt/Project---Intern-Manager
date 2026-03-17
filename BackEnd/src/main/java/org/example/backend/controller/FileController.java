package org.example.backend.controller;

import org.example.backend.dto.FileResponse;
import org.example.backend.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

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
