package org.example.backend.service.impl;

import org.example.backend.dto.TaskRequest;
import org.example.backend.dto.TaskResponse;
import org.example.backend.repository.InternRepository;
import org.example.backend.repository.TaskRepository;
import org.example.backend.repository.MentorRepository;
import org.example.backend.entity.Mentor;
import org.example.backend.entity.Intern;
import org.example.backend.entity.Task;
import org.example.backend.service.ITaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;
    private final MentorRepository mentorRepository;
    private final InternRepository internRepository;

    /**
     * Lấy danh sách nhiệm vụ (Task) có phân trang và lọc theo tiêu chí.
     * 
     * @param title (Tùy chọn) Tiêu đề nhiệm vụ để tìm kiếm.
     * @param status (Tùy chọn) Trạng thái nhiệm vụ.
     * @param internId (Tùy chọn) ID của thực tập sinh được giao.
     * @param mentorId (Tùy chọn) ID của người hướng dẫn tạo nhiệm vụ.
     * @param pageable Thông tin phân trang.
     * @return Page<TaskResponse> Trang danh sách nhiệm vụ.
     */
    public Page<TaskResponse> getAllTasks(String title, String status, Long internId, Long mentorId, Pageable pageable) {
        try {
            Specification<Task> spec = (root, query, cb) -> {
                var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

                if (StringUtils.hasText(title)) {
                    predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
                }

                if (StringUtils.hasText(status)) {
                    predicates.add(cb.equal(root.get("status"), status));
                }

                if (internId != null) {
                    predicates.add(cb.equal(root.get("intern").get("id"), internId));
                }

                if (mentorId != null) {
                    predicates.add(cb.equal(root.get("mentor").get("id"), mentorId));
                }

                return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
            };

            return taskRepository.findAll(spec, pageable).map(this::mapToResponse);
        } catch (Exception e) {
            System.err.println("❌ TaskService Error in getAllTasks: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Lấy danh sách các nhiệm vụ được giao cho một thực tập sinh cụ thể.
     * 
     * @param internId ID của thực tập sinh.
     * @return List<TaskResponse> Danh sách nhiệm vụ của thực tập sinh.
     */
    public List<TaskResponse> getTasksByInternId(Long internId) {
        return taskRepository.findByInternId(internId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy thông tin chi tiết của một nhiệm vụ theo ID.
     * 
     * @param id ID của nhiệm vụ.
     * @return TaskResponse Thông tin chi tiết nhiệm vụ.
     */
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToResponse(task);
    }

    /**
     * Tạo mới một nhiệm vụ và giao cho thực tập sinh (nếu có).
     * 
     * @param request Thông tin chi tiết của nhiệm vụ mới.
     * @return TaskResponse Thông tin nhiệm vụ vừa tạo.
     */
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        try {
            Intern intern = null;
            if (request.getInternId() != null) {
                intern = internRepository.findById(request.getInternId()).orElse(null);
            }

            Mentor mentor = null;
            if (request.getMentorId() != null) {
                mentor = mentorRepository.findById(request.getMentorId()).orElse(null);
            }

            Task task = Task.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .status(request.getStatus())
                    .priority(request.getPriority())
                    .deadline(request.getDeadline())
                    .intern(intern)
                    .mentor(mentor)
                    .build();

            return mapToResponse(taskRepository.save(task));
        } catch (Exception e) {
            System.err.println("❌ TaskService Error in createTask: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Cập nhật thông tin của một nhiệm vụ hiện có.
     * 
     * @param id ID của nhiệm vụ cần cập nhật.
     * @param request Thông tin cập nhật mới.
     * @return TaskResponse Thông tin nhiệm vụ sau khi cập nhật.
     */
    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        
        Intern intern = null;
        if (request.getInternId() != null) {
            intern = internRepository.findById(request.getInternId()).orElse(null);
        }

        Mentor mentor = null;
        if (request.getMentorId() != null) {
            mentor = mentorRepository.findById(request.getMentorId()).orElse(null);
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());
        task.setIntern(intern);
        task.setMentor(mentor);

        return mapToResponse(taskRepository.save(task));
    }

    /**
     * Xóa một nhiệm vụ khỏi hệ thống theo ID.
     * 
     * @param id ID của nhiệm vụ cần xóa.
     */
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    private TaskResponse mapToResponse(Task task) {
        Long internId = (task.getIntern() != null) ? task.getIntern().getId() : null;
        Long mentorId = (task.getMentor() != null) ? task.getMentor().getId() : null;
        String internName = (task.getIntern() != null) ? task.getIntern().getFullName() : null;
        String mentorName = (task.getMentor() != null) ? task.getMentor().getFullName() : null;

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .deadline(task.getDeadline())
                .internId(internId)
                .mentorId(mentorId)
                .internName(internName)
                .mentorName(mentorName)
                .build();
    }
}
