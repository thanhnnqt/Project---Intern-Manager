package org.example.backend.service.impl;

import org.example.backend.dto.DashboardResponse;
import org.example.backend.dto.InternResponse;
import org.example.backend.dto.TaskResponse;
import org.example.backend.entity.Intern;
import org.example.backend.entity.Task;
import org.example.backend.repository.InternRepository;
import org.example.backend.repository.TaskRepository;
import org.example.backend.repository.MentorRepository;
import org.example.backend.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService implements IDashboardService {

    private final InternRepository internRepository;
    private final TaskRepository taskRepository;
    private final MentorRepository mentorRepository;

    /**
     * Lấy dữ liệu tổng quan cho trang Dashboard.
     * Bao gồm: tổng số thực tập sinh, mentor, task, số task đã hoàn thành, 
     * danh sách 5 thực tập sinh mới nhất và 5 task mới nhất.
     * 
     * @return DashboardResponse Chứa toàn bộ dữ liệu thống kê và danh sách gần đây.
     */
    public DashboardResponse getDashboardData() {
        long totalInterns = internRepository.count();
        long totalMentors = mentorRepository.count();
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByStatus("COMPLETED");

        List<InternResponse> recentInterns = internRepository.findTop5ByOrderByIdDesc().stream()
                .map(this::mapToInternResponse)
                .collect(Collectors.toList());

        List<TaskResponse> recentTasks = taskRepository.findTop5ByOrderByIdDesc().stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalInterns(totalInterns)
                .totalMentors(totalMentors)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .recentInterns(recentInterns)
                .recentTasks(recentTasks)
                .build();
    }

    private InternResponse mapToInternResponse(Intern intern) {
        return InternResponse.builder()
                .id(intern.getId())
                .name(intern.getFullName())
                .email(intern.getEmail())
                .phone(intern.getPhone())
                .avatar(intern.getAvatar())
                .university(intern.getUniversity())
                .major(intern.getMajor())
                .status(intern.getStatus())
                .mentorId(intern.getMentor() != null ? intern.getMentor().getId() : null)
                .mentorName(intern.getMentor() != null ? intern.getMentor().getFullName() : null)
                .build();
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .deadline(task.getDeadline())
                .internName(task.getIntern() != null ? task.getIntern().getFullName() : null)
                .mentorName(task.getMentor() != null ? task.getMentor().getFullName() : null)
                .build();
    }
}
