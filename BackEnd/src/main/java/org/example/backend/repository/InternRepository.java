package org.example.backend.repository;

import org.example.backend.entity.Intern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternRepository extends JpaRepository<Intern, Long>, JpaSpecificationExecutor<Intern> {
    List<Intern> findTop5ByOrderByIdDesc();
    long countByMentorId(Long mentorId);
    java.util.Optional<Intern> findByAccountUsername(String username);
    java.util.Optional<Intern> findByEmail(String email);
}
