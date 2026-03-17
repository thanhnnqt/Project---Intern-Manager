package org.example.backend.repository;

import org.example.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    long countByStatus(String status);
    List<Task> findTop5ByOrderByIdDesc();
    java.util.List<Task> findByInternId(Long internId);
}
