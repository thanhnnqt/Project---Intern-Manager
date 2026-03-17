package org.example.backend.controller;

import org.example.backend.dto.MentorRequest;
import org.example.backend.dto.MentorResponse;
import org.example.backend.service.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
public class MentorController {

    @Autowired
    private MentorService mentorService;

    @GetMapping
    public ResponseEntity<List<MentorResponse>> getAllMentors() {
        return ResponseEntity.ok(mentorService.getAllMentors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentorResponse> getMentorById(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.getMentorById(id));
    }

    @PostMapping
    public ResponseEntity<MentorResponse> createMentor(@RequestBody MentorRequest request) {
        return ResponseEntity.ok(mentorService.createMentor(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MentorResponse> updateMentor(@PathVariable Long id, @RequestBody MentorRequest request) {
        return ResponseEntity.ok(mentorService.updateMentor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMentor(@PathVariable Long id) {
        mentorService.deleteMentor(id);
        return ResponseEntity.ok().build();
    }
}
