package org.example.backend.dto;

import lombok.Data;

@Data
public class MentorRequest {
    private String name;
    private String email;
    private String department;
    private String position;
    private String avatar;
}
