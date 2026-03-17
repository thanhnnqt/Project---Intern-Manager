package org.example.backend.controller;

import org.example.backend.dto.InternRequest;
import org.example.backend.dto.InternResponse;
import org.example.backend.service.InternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interns")
public class InternController {

    @Autowired
    private InternService internService;

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<InternResponse>> getAllInterns(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String university,
            @RequestParam(required = false) Long mentorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(internService.getAllInterns(name, university, mentorId, status, page, size, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternResponse> getInternById(@PathVariable Long id) {
        return ResponseEntity.ok(internService.getInternById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InternResponse> createIntern(@jakarta.validation.Valid @RequestBody InternRequest request) {
        System.out.println("📥 InternController: Target hit - Creating intern for email: " + request.getEmail());
        return ResponseEntity.ok(internService.createIntern(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InternResponse> updateIntern(@PathVariable Long id, @jakarta.validation.Valid @RequestBody InternRequest request) {
        return ResponseEntity.ok(internService.updateIntern(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteIntern(@PathVariable Long id) {
        internService.deleteIntern(id);
        return ResponseEntity.ok().build();
    }
}
