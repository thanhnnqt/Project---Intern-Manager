package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String university;
    private String major;
    private String mentorName;
    private Long mentorId;
    private String status;
    private String avatar;
    private Long accountId;
}
