package org.example.backend.service;

import org.example.backend.dto.DashboardResponse;
import org.example.backend.dto.InternResponse;
import org.example.backend.dto.TaskResponse;
import org.example.backend.entity.Intern;
import org.example.backend.entity.Task;
import org.example.backend.repository.InternRepository;
import org.example.backend.repository.TaskRepository;
import org.example.backend.repository.MentorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private InternRepository internRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MentorRepository mentorRepository;

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
