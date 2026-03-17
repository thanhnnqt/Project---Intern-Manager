package org.example.backend.controller;

import org.example.backend.dto.DashboardResponse;
import org.example.backend.dto.InternResponse;
import org.example.backend.dto.TaskResponse;
import org.example.backend.repository.InternRepository;
import org.example.backend.repository.TaskRepository;
import org.example.backend.service.DashboardService;
import org.example.backend.service.InternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private InternService internService;
    
    @Autowired
    private InternRepository internRepository;
    
    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }

    @GetMapping("/tasks/recent")
    public ResponseEntity<List<TaskResponse>> getRecentTasks() {
        return ResponseEntity.ok(dashboardService.getDashboardData().getRecentTasks());
    }

    @GetMapping("/interns/recent")
    public ResponseEntity<List<InternResponse>> getRecentInterns() {
        return ResponseEntity.ok(dashboardService.getDashboardData().getRecentInterns());
    }
}
