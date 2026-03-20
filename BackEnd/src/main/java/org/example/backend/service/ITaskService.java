package org.example.backend.service;

import org.example.backend.dto.TaskRequest;
import org.example.backend.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ITaskService {
    Page<TaskResponse> getAllTasks(String title, String status, Long internId, Long mentorId, Pageable pageable);
    List<TaskResponse> getTasksByInternId(Long internId);
    TaskResponse getTaskById(Long id);
    TaskResponse createTask(TaskRequest request);
    TaskResponse updateTask(Long id, TaskRequest request);
    void deleteTask(Long id);
}
