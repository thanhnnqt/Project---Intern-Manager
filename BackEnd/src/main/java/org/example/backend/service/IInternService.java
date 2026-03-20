package org.example.backend.service;

import org.example.backend.dto.InternRequest;
import org.example.backend.dto.InternResponse;
import org.springframework.data.domain.Page;

public interface IInternService {
    Page<InternResponse> getAllInterns(String name, String university, Long mentorId, String status, int page, int size, String sortBy);
    InternResponse getInternById(Long id);
    InternResponse createIntern(InternRequest request);
    InternResponse updateIntern(Long id, InternRequest request);
    void deleteIntern(Long id);
}
