package org.example.backend.service;

import org.example.backend.dto.MentorRequest;
import org.example.backend.dto.MentorResponse;
import org.springframework.data.domain.Page;

public interface IMentorService {
    Page<MentorResponse> getAllMentors(String name, String department, int page, int size, String sortBy);
    MentorResponse getMentorById(Long id);
    MentorResponse createMentor(MentorRequest request);
    MentorResponse updateMentor(Long id, MentorRequest request);
    void deleteMentor(Long id);
}
