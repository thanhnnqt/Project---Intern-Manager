package org.example.backend.repository;

import org.example.backend.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long>, JpaSpecificationExecutor<Mentor> {
    Optional<Mentor> findByEmail(String email);
    Optional<Mentor> findByAccountUsername(String username);
}
