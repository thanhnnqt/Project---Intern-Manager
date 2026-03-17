package org.example.backend.repository;

import org.example.backend.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByTaskId(Long taskId);
    List<File> findByInternId(Long internId);
}
