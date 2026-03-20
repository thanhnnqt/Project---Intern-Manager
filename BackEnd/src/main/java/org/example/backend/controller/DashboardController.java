package org.example.backend.controller;

import org.example.backend.dto.DashboardResponse;
import org.example.backend.dto.InternResponse;
import org.example.backend.dto.TaskResponse;
import org.example.backend.service.IDashboardService;
import org.example.backend.service.IInternService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;
    private final IInternService internService;

    /**
     * Endpoint lấy dữ liệu tổng hợp cho trang Dashboard.
     * 
     * @return ResponseEntity chứa thông tin thống kê và danh sách gần đây.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }

    /**
     * Endpoint lấy danh sách 5 task mới nhất.
     * 
     * @return ResponseEntity chứa danh sách task gần đây.
     */
    @GetMapping("/tasks/recent")
    public ResponseEntity<List<TaskResponse>> getRecentTasks() {
        return ResponseEntity.ok(dashboardService.getDashboardData().getRecentTasks());
    }

    /**
     * Endpoint lấy danh sách 5 thực tập sinh mới nhất.
     * 
     * @return ResponseEntity chứa danh sách thực tập sinh gần đây.
     */
    @GetMapping("/interns/recent")
    public ResponseEntity<List<InternResponse>> getRecentInterns() {
        return ResponseEntity.ok(dashboardService.getDashboardData().getRecentInterns());
    }
}
